package com.pelvictrainer.data.sync

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.pelvictrainer.domain.repository.TrainingRepository
import com.pelvictrainer.network.PelvicApi
import com.pelvictrainer.network.SessionLogRequest
import com.pelvictrainer.network.TokenStorage
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val api: PelvicApi,
    private val tokenStorage: TokenStorage,
    private val trainingRepository: TrainingRepository,
) : CoroutineWorker(context, params) {

    companion object {
        private const val TAG = "SyncWorker"
    }

    override suspend fun doWork(): Result {
        // Синхронизируем только если пользователь залогинен
        if (!tokenStorage.isLoggedIn) {
            Log.d(TAG, "Пропуск: пользователь не залогинен")
            return Result.success()
        }

        return try {
            val unsynced = trainingRepository.getUnsyncedSessions()
            if (unsynced.isEmpty()) {
                Log.d(TAG, "Нет тренировок для синхронизации")
                return Result.success()
            }

            Log.d(TAG, "Синхронизируем ${unsynced.size} тренировок")

            var successCount = 0
            for (session in unsynced) {
                try {
                    val response = api.logSession(
                        SessionLogRequest(
                            presetId = session.presetId.toInt(),
                            durationSeconds = session.durationSeconds.toInt(),
                            repeatsCompleted = session.repeats,
                        )
                    )
                    trainingRepository.markAsSynced(session.id, response.sessionId)
                    successCount++
                    Log.d(TAG, "✅ Тренировка #${session.id} синхронизирована (serverId=${response.sessionId})")
                } catch (e: Exception) {
                    Log.e(TAG, "❌ Ошибка синхронизации тренировки #${session.id}: ${e.message}")
                    // Не прерываем цикл — пробуем остальные
                }
            }

            Log.d(TAG, "Готово: $successCount/${unsynced.size} тренировок синхронизировано")

            if (successCount == 0 && unsynced.isNotEmpty()) {
                // Все тренировки не удалось отправить — retry позже
                Result.retry()
            } else {
                Result.success()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Критическая ошибка синхронизации: ${e.message}")
            if (runAttemptCount > 3) Result.failure() else Result.retry()
        }
    }
}
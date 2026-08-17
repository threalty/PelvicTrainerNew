package com.pelvictrainer.data.sync

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
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
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        // Синхронизируем только если пользователь залогинен
        if (!tokenStorage.isLoggedIn) return Result.success()

        return try {
            // TODO: заменить на ваш DAO — здесь пример с pending_sessions
            // val pending = pendingSessionsDao.getAll()
            // for (session in pending) {
            //     api.logSession(SessionLogRequest(
            //         presetId = session.presetId,
            //         durationSeconds = session.durationSeconds,
            //         repeatsCompleted = session.repeatsCompleted,
            //     ))
            //     pendingSessionsDao.delete(session.id)
            // }
            Result.success()
        } catch (e: Exception) {
            if (runAttemptCount > 3) Result.failure() else Result.retry()
        }
    }
}
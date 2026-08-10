package com.pelvictrainer.workouts

import com.pelvictrainer.domain.model.TrainingLevel
import com.pelvictrainer.domain.model.TrainingPreset
import com.pelvictrainer.domain.model.UserPreferences
import com.pelvictrainer.domain.repository.TrainingRepository
import com.pelvictrainer.domain.repository.UserPreferencesRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class WorkoutsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var trainingRepository: TrainingRepository
    private lateinit var userPreferencesRepository: UserPreferencesRepository
    private val prefsFlow = MutableStateFlow(UserPreferences())
    private val sessionsFlow = MutableStateFlow<List<Any>>(emptyList())

    private val testPresets = listOf(
        TrainingPreset(1, "Новичок", "Базовый", TrainingLevel.BEGINNER, 3, 3, 5, 10),
        TrainingPreset(2, "Любитель", "Средний", TrainingLevel.INTERMEDIATE, 5, 5, 5, 15),
        TrainingPreset(3, "Профи", "Максимум", TrainingLevel.ADVANCED, 8, 8, 4, 20)
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        trainingRepository = mockk(relaxed = true)
        userPreferencesRepository = mockk(relaxed = true)

        coEvery { trainingRepository.getPresets() } returns flowOf(testPresets)
        coEvery { userPreferencesRepository.userPreferences } returns prefsFlow
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(sessionCount: Int = 0): WorkoutsViewModel {
        val mockSessions = List(sessionCount) { mockk<com.pelvictrainer.domain.model.TrainingSession>() }
        coEvery { trainingRepository.getSessions() } returns flowOf(mockSessions)
        return WorkoutsViewModel(trainingRepository, userPreferencesRepository)
    }

    // ===== Тесты начального состояния =====

    @Test
    fun `initial state is loading`() = runTest {
        prefsFlow.value = UserPreferences(trainingLevel = TrainingLevel.BEGINNER)
        val viewModel = createViewModel(0)

        assertTrue(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `loads presets and user level after init`() = runTest {
        prefsFlow.value = UserPreferences(trainingLevel = TrainingLevel.INTERMEDIATE)
        val viewModel = createViewModel(0)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(3, state.presets.size)
        assertEquals(TrainingLevel.INTERMEDIATE, state.userLevel)
    }

    // ===== Тесты порогов автоповышения =====

    @Test
    fun `stays BEGINNER with 0 trainings`() = runTest {
        prefsFlow.value = UserPreferences(trainingLevel = TrainingLevel.BEGINNER)
        val viewModel = createViewModel(0)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(TrainingLevel.BEGINNER, viewModel.uiState.value.userLevel)
        assertEquals(10, viewModel.uiState.value.trainingsNeededForNextLevel)
    }

    @Test
    fun `stays BEGINNER with 9 trainings`() = runTest {
        prefsFlow.value = UserPreferences(trainingLevel = TrainingLevel.BEGINNER)
        val viewModel = createViewModel(9)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(TrainingLevel.BEGINNER, viewModel.uiState.value.userLevel)
        assertEquals(9, viewModel.uiState.value.trainingsDoneOnCurrentLevel)
    }

    @Test
    fun `auto upgrades to INTERMEDIATE at 10 trainings`() = runTest {
        prefsFlow.value = UserPreferences(trainingLevel = TrainingLevel.BEGINNER)
        val viewModel = createViewModel(10)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(TrainingLevel.INTERMEDIATE, viewModel.uiState.value.userLevel)
        coVerify { userPreferencesRepository.updateTrainingLevel(TrainingLevel.INTERMEDIATE) }
    }

    @Test
    fun `emits levelUpEvent when upgrading to INTERMEDIATE`() = runTest {
        prefsFlow.value = UserPreferences(trainingLevel = TrainingLevel.BEGINNER)
        val viewModel = createViewModel(10)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(TrainingLevel.INTERMEDIATE, viewModel.levelUpEvent.value)
    }

    @Test
    fun `stays INTERMEDIATE with 15 trainings`() = runTest {
        prefsFlow.value = UserPreferences(trainingLevel = TrainingLevel.INTERMEDIATE)
        val viewModel = createViewModel(15)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(TrainingLevel.INTERMEDIATE, viewModel.uiState.value.userLevel)
        assertEquals(15, viewModel.uiState.value.trainingsDoneOnCurrentLevel)
        assertEquals(30, viewModel.uiState.value.trainingsNeededForNextLevel)
    }

    @Test
    fun `auto upgrades to ADVANCED at 30 trainings`() = runTest {
        prefsFlow.value = UserPreferences(trainingLevel = TrainingLevel.INTERMEDIATE)
        val viewModel = createViewModel(30)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(TrainingLevel.ADVANCED, viewModel.uiState.value.userLevel)
        coVerify { userPreferencesRepository.updateTrainingLevel(TrainingLevel.ADVANCED) }
    }

    @Test
    fun `stays ADVANCED with 50 trainings`() = runTest {
        prefsFlow.value = UserPreferences(trainingLevel = TrainingLevel.ADVANCED)
        val viewModel = createViewModel(50)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(TrainingLevel.ADVANCED, viewModel.uiState.value.userLevel)
        assertNull(viewModel.uiState.value.nextLevel)
    }

    // ===== Тесты расчёта прогресса =====

    @Test
    fun `calculates progress for BEGINNER correctly`() = runTest {
        prefsFlow.value = UserPreferences(trainingLevel = TrainingLevel.BEGINNER)
        val viewModel = createViewModel(5)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(5, state.trainingsDoneOnCurrentLevel)
        assertEquals(10, state.trainingsNeededForNextLevel)
        assertEquals(0.5f, state.progressToNextLevel, 0.01f)
        assertEquals(TrainingLevel.INTERMEDIATE, state.nextLevel)
    }

    @Test
    fun `calculates progress for INTERMEDIATE correctly`() = runTest {
        prefsFlow.value = UserPreferences(trainingLevel = TrainingLevel.INTERMEDIATE)
        val viewModel = createViewModel(20)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(20, state.trainingsDoneOnCurrentLevel)
        assertEquals(30, state.trainingsNeededForNextLevel)
        assertEquals(20f / 30f, state.progressToNextLevel, 0.01f)
        assertEquals(TrainingLevel.ADVANCED, state.nextLevel)
    }

    @Test
    fun `shows max level reached for ADVANCED`() = runTest {
        prefsFlow.value = UserPreferences(trainingLevel = TrainingLevel.ADVANCED)
        val viewModel = createViewModel(35)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertNull(state.nextLevel)
        assertEquals(1f, state.progressToNextLevel, 0.01f)
    }

    @Test
    fun `totalCompletedTrainings reflects session count`() = runTest {
        prefsFlow.value = UserPreferences(trainingLevel = TrainingLevel.BEGINNER)
        val viewModel = createViewModel(15)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(15, viewModel.uiState.value.totalCompletedTrainings)
    }

    // ===== Тесты levelUpEvent =====

    @Test
    fun `onLevelUpShown clears levelUpEvent`() = runTest {
        prefsFlow.value = UserPreferences(trainingLevel = TrainingLevel.BEGINNER)
        val viewModel = createViewModel(10)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(TrainingLevel.INTERMEDIATE, viewModel.levelUpEvent.value)

        viewModel.onLevelUpShown()

        assertNull(viewModel.levelUpEvent.value)
    }

    @Test
    fun `no levelUpEvent when user already at correct level`() = runTest {
        prefsFlow.value = UserPreferences(trainingLevel = TrainingLevel.INTERMEDIATE)
        val viewModel = createViewModel(15)
        testDispatcher.scheduler.advanceUntilIdle()

        assertNull(viewModel.levelUpEvent.value)
    }

    // ===== Тесты рекомендаций =====

    @Test
    fun `marks BEGINNER preset as recommended for BEGINNER user`() = runTest {
        prefsFlow.value = UserPreferences(trainingLevel = TrainingLevel.BEGINNER)
        val viewModel = createViewModel(0)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        val beginnerPreset = state.presets.first { it.level == TrainingLevel.BEGINNER }
        assertEquals(TrainingLevel.BEGINNER, state.userLevel)
        assertEquals(TrainingLevel.BEGINNER, beginnerPreset.level)
    }
}
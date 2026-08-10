package com.pelvictrainer.app

import com.pelvictrainer.domain.model.TrainingSession
import com.pelvictrainer.domain.model.UserPreferences
import com.pelvictrainer.domain.repository.TrainingRepository
import com.pelvictrainer.domain.repository.UserPreferencesRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDate
import java.time.ZoneId

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var trainingRepository: TrainingRepository
    private lateinit var userPreferencesRepository: UserPreferencesRepository
    private val prefsFlow = MutableStateFlow(UserPreferences())
    private val sessionsFlow = MutableStateFlow<List<TrainingSession>>(emptyList())

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        trainingRepository = mockk()
        userPreferencesRepository = mockk()

        coEvery { trainingRepository.getSessions() } returns sessionsFlow
        coEvery { userPreferencesRepository.userPreferences } returns prefsFlow
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(): HomeViewModel {
        return HomeViewModel(trainingRepository, userPreferencesRepository)
    }

    private fun createSession(date: LocalDate): TrainingSession {
        val timestamp = date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli() + 12 * 60 * 60 * 1000
        return TrainingSession(
            id = 0L,
            date = timestamp,
            presetId = 1L,
            repeats = 10,
            durationSeconds = 120L
        )
    }

    // ===== Тесты начального состояния =====

    @Test
    fun `initial state has default values`() = runTest {
        prefsFlow.value = UserPreferences(weeklyGoal = 3)
        sessionsFlow.value = emptyList()

        val viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(3, state.weeklyGoal)
        assertEquals(0, state.completedThisWeek)
        assertEquals(0, state.currentStreak)
        assertEquals(0, state.totalTrainings)
    }

    // ===== Тесты недельного прогресса =====

    @Test
    fun `counts only sessions from last 7 days`() = runTest {
        val today = LocalDate.now()
        prefsFlow.value = UserPreferences(weeklyGoal = 5)
        sessionsFlow.value = listOf(
            createSession(today),
            createSession(today.minusDays(1)),
            createSession(today.minusDays(6)),
            createSession(today.minusDays(7)),
            createSession(today.minusDays(30))
        )

        val viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(3, viewModel.uiState.value.completedThisWeek)
        assertEquals(5, viewModel.uiState.value.totalTrainings)
    }

    @Test
    fun `progress is calculated correctly`() = runTest {
        val today = LocalDate.now()
        prefsFlow.value = UserPreferences(weeklyGoal = 4)
        sessionsFlow.value = listOf(
            createSession(today),
            createSession(today.minusDays(1)),
            createSession(today.minusDays(2))
        )

        val viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(3, state.completedThisWeek)
        assertEquals(4, state.weeklyGoal)
        assertEquals(0.75f, state.progress, 0.01f)
    }

    @Test
    fun `goal reached when completed equals goal`() = runTest {
        val today = LocalDate.now()
        prefsFlow.value = UserPreferences(weeklyGoal = 3)
        sessionsFlow.value = listOf(
            createSession(today),
            createSession(today.minusDays(1)),
            createSession(today.minusDays(2))
        )

        val viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.uiState.value.isGoalReached)
        assertEquals(0, viewModel.uiState.value.remainingTrainings)
    }

    @Test
    fun `goal reached when completed exceeds goal`() = runTest {
        val today = LocalDate.now()
        prefsFlow.value = UserPreferences(weeklyGoal = 2)
        sessionsFlow.value = listOf(
            createSession(today),
            createSession(today.minusDays(1)),
            createSession(today.minusDays(2)),
            createSession(today.minusDays(3))
        )

        val viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.uiState.value.isGoalReached)
        assertEquals(0, viewModel.uiState.value.remainingTrainings)
        assertEquals(1f, viewModel.uiState.value.progress, 0.01f)
    }

    @Test
    fun `goal not reached when completed less than goal`() = runTest {
        val today = LocalDate.now()
        prefsFlow.value = UserPreferences(weeklyGoal = 5)
        sessionsFlow.value = listOf(
            createSession(today),
            createSession(today.minusDays(1))
        )

        val viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isGoalReached)
        assertEquals(3, state.remainingTrainings)
    }

    // ===== Тесты расчёта streak =====

    @Test
    fun `streak is 0 when no sessions`() = runTest {
        prefsFlow.value = UserPreferences(weeklyGoal = 3)
        sessionsFlow.value = emptyList()

        val viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(0, viewModel.uiState.value.currentStreak)
    }

    @Test
    fun `streak is 1 when trained today`() = runTest {
        val today = LocalDate.now()
        prefsFlow.value = UserPreferences(weeklyGoal = 3)
        sessionsFlow.value = listOf(createSession(today))

        val viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(1, viewModel.uiState.value.currentStreak)
    }

    @Test
    fun `streak is 1 when trained yesterday only`() = runTest {
        val yesterday = LocalDate.now().minusDays(1)
        prefsFlow.value = UserPreferences(weeklyGoal = 3)
        sessionsFlow.value = listOf(createSession(yesterday))

        val viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(1, viewModel.uiState.value.currentStreak)
    }

    @Test
    fun `streak is 0 when last training was 2 days ago`() = runTest {
        val twoDaysAgo = LocalDate.now().minusDays(2)
        prefsFlow.value = UserPreferences(weeklyGoal = 3)
        sessionsFlow.value = listOf(createSession(twoDaysAgo))

        val viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(0, viewModel.uiState.value.currentStreak)
    }

    @Test
    fun `streak counts consecutive days correctly`() = runTest {
        val today = LocalDate.now()
        prefsFlow.value = UserPreferences(weeklyGoal = 7)
        sessionsFlow.value = listOf(
            createSession(today),
            createSession(today.minusDays(1)),
            createSession(today.minusDays(2)),
            createSession(today.minusDays(3)),
            createSession(today.minusDays(4))
        )

        val viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(5, viewModel.uiState.value.currentStreak)
    }

    @Test
    fun `streak breaks on gap day`() = runTest {
        val today = LocalDate.now()
        prefsFlow.value = UserPreferences(weeklyGoal = 7)
        sessionsFlow.value = listOf(
            createSession(today),
            createSession(today.minusDays(1)),
            createSession(today.minusDays(3)),
            createSession(today.minusDays(4))
        )

        val viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(2, viewModel.uiState.value.currentStreak)
    }

    @Test
    fun `streak ignores duplicate sessions on same day`() = runTest {
        val today = LocalDate.now()
        prefsFlow.value = UserPreferences(weeklyGoal = 7)
        sessionsFlow.value = listOf(
            createSession(today),
            createSession(today),
            createSession(today),
            createSession(today.minusDays(1)),
            createSession(today.minusDays(2))
        )

        val viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(3, viewModel.uiState.value.currentStreak)
    }

    // ===== Тесты totalTrainings =====

    @Test
    fun `totalTrainings counts all sessions regardless of date`() = runTest {
        val today = LocalDate.now()
        prefsFlow.value = UserPreferences(weeklyGoal = 3)
        sessionsFlow.value = listOf(
            createSession(today),
            createSession(today.minusDays(10)),
            createSession(today.minusDays(30)),
            createSession(today.minusDays(100)),
            createSession(today.minusDays(365))
        )

        val viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(5, viewModel.uiState.value.totalTrainings)
        assertEquals(1, viewModel.uiState.value.completedThisWeek)
    }

    // ===== Тесты реактивности =====

    @Test
    fun `state updates when preferences change`() = runTest {
        prefsFlow.value = UserPreferences(weeklyGoal = 3)
        sessionsFlow.value = emptyList()
        val viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(3, viewModel.uiState.value.weeklyGoal)

        prefsFlow.value = UserPreferences(weeklyGoal = 7)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(7, viewModel.uiState.value.weeklyGoal)
    }

    @Test
    fun `state updates when sessions change`() = runTest {
        val today = LocalDate.now()
        prefsFlow.value = UserPreferences(weeklyGoal = 5)
        sessionsFlow.value = emptyList()
        val viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(0, viewModel.uiState.value.totalTrainings)

        sessionsFlow.value = listOf(
            createSession(today),
            createSession(today.minusDays(1))
        )
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(2, viewModel.uiState.value.totalTrainings)
        assertEquals(2, viewModel.uiState.value.completedThisWeek)
    }

    @Test
    fun `progress is capped at 1 point 0 even when exceeded`() = runTest {
        val today = LocalDate.now()
        prefsFlow.value = UserPreferences(weeklyGoal = 2)
        sessionsFlow.value = listOf(
            createSession(today),
            createSession(today.minusDays(1)),
            createSession(today.minusDays(2)),
            createSession(today.minusDays(3)),
            createSession(today.minusDays(4))
        )

        val viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(1f, viewModel.uiState.value.progress, 0.01f)
    }
}
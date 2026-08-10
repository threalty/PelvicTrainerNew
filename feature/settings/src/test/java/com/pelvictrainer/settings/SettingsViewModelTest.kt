package com.pelvictrainer.settings

import com.pelvictrainer.domain.model.AccentColor
import com.pelvictrainer.domain.model.BackgroundSound
import com.pelvictrainer.domain.model.ReminderConfig
import com.pelvictrainer.domain.model.ThemeMode
import com.pelvictrainer.domain.model.UserPreferences
import com.pelvictrainer.domain.repository.UserPreferencesRepository
import io.mockk.coEvery
import io.mockk.coVerify
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
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: UserPreferencesRepository
    private lateinit var viewModel: SettingsViewModel
    private val prefsFlow = MutableStateFlow(UserPreferences())

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk(relaxed = true)
        coEvery { repository.userPreferences } returns prefsFlow
        viewModel = SettingsViewModel(repository)
        testDispatcher.scheduler.advanceUntilIdle()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state has default preferences`() = runTest {
        val defaultPrefs = UserPreferences()
        prefsFlow.value = defaultPrefs
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(defaultPrefs, viewModel.uiState.value)
    }

    @Test
    fun `updateThemeMode calls repository with correct value`() = runTest {
        viewModel.updateThemeMode(ThemeMode.LIGHT)
        testDispatcher.scheduler.advanceUntilIdle()
        coVerify { repository.updateThemeMode(ThemeMode.LIGHT) }
    }

    @Test
    fun `updateAccentColor calls repository with correct value`() = runTest {
        viewModel.updateAccentColor(AccentColor.BORDEAUX)
        testDispatcher.scheduler.advanceUntilIdle()
        coVerify { repository.updateAccentColor(AccentColor.BORDEAUX) }
    }

    @Test
    fun `updateVoiceEnabled calls repository with correct value`() = runTest {
        viewModel.updateVoiceEnabled(false)
        testDispatcher.scheduler.advanceUntilIdle()
        coVerify { repository.updateVoiceEnabled(false) }
    }

    @Test
    fun `updateVoiceVolume calls repository with correct value`() = runTest {
        viewModel.updateVoiceVolume(0.5f)
        testDispatcher.scheduler.advanceUntilIdle()
        coVerify { repository.updateVoiceVolume(0.5f) }
    }

    @Test
    fun `updateVibrationEnabled calls repository with correct value`() = runTest {
        viewModel.updateVibrationEnabled(false)
        testDispatcher.scheduler.advanceUntilIdle()
        coVerify { repository.updateVibrationEnabled(false) }
    }

    @Test
    fun `updateVibrationIntensity calls repository with correct value`() = runTest {
        viewModel.updateVibrationIntensity(0.7f)
        testDispatcher.scheduler.advanceUntilIdle()
        coVerify { repository.updateVibrationIntensity(0.7f) }
    }

    @Test
    fun `updateRemindersEnabled calls repository with correct value`() = runTest {
        viewModel.updateRemindersEnabled(true)
        testDispatcher.scheduler.advanceUntilIdle()
        coVerify { repository.updateRemindersEnabled(true) }
    }

    @Test
    fun `updateWeeklyGoal calls repository with correct value`() = runTest {
        viewModel.updateWeeklyGoal(5)
        testDispatcher.scheduler.advanceUntilIdle()
        coVerify { repository.updateWeeklyGoal(5) }
    }

    @Test
    fun `updateBackgroundSound calls repository with correct value`() = runTest {
        viewModel.updateBackgroundSound(BackgroundSound.RAIN)
        testDispatcher.scheduler.advanceUntilIdle()
        coVerify { repository.updateBackgroundSound(BackgroundSound.RAIN) }
    }

    @Test
    fun `addReminderTime adds new time to list`() = runTest {
        val initialPrefs = UserPreferences(reminderTimes = emptyList())
        prefsFlow.value = initialPrefs
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.addReminderTime(8, 30)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { repository.addReminderTime(ReminderConfig(8, 30)) }
    }

    @Test
    fun `addReminderTime does not add duplicate time`() = runTest {
        val existingConfig = ReminderConfig(8, 30)
        val initialPrefs = UserPreferences(reminderTimes = listOf(existingConfig))
        prefsFlow.value = initialPrefs
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.addReminderTime(8, 30)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify(exactly = 0) { repository.addReminderTime(any()) }
    }

    @Test
    fun `removeReminderTime calls repository with correct config`() = runTest {
        val config = ReminderConfig(8, 30)
        viewModel.removeReminderTime(config)
        testDispatcher.scheduler.advanceUntilIdle()
        coVerify { repository.removeReminderTime(config) }
    }

    @Test
    fun `toggleReminderDay adds day when not present`() = runTest {
        val initialPrefs = UserPreferences(reminderDaysOfWeek = listOf(1, 2, 3))
        prefsFlow.value = initialPrefs
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.toggleReminderDay(4)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { repository.updateReminderDaysOfWeek(listOf(1, 2, 3, 4)) }
    }

    @Test
    fun `toggleReminderDay removes day when present and more than one day`() = runTest {
        val initialPrefs = UserPreferences(reminderDaysOfWeek = listOf(1, 2, 3, 4))
        prefsFlow.value = initialPrefs
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.toggleReminderDay(2)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { repository.updateReminderDaysOfWeek(listOf(1, 3, 4)) }
    }

    @Test
    fun `toggleReminderDay does not remove last day`() = runTest {
        val initialPrefs = UserPreferences(reminderDaysOfWeek = listOf(1))
        prefsFlow.value = initialPrefs
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.toggleReminderDay(1)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { repository.updateReminderDaysOfWeek(listOf(1)) }
    }

    @Test
    fun `viewModel collects userPreferences updates`() = runTest {
        val newPrefs = UserPreferences(
            themeMode = ThemeMode.LIGHT,
            voiceEnabled = false,
            weeklyGoal = 5
        )

        prefsFlow.value = newPrefs
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(newPrefs, viewModel.uiState.value)
    }
}
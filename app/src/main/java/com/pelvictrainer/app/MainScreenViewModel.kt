package com.pelvictrainer.app

import androidx.lifecycle.ViewModel
import com.pelvictrainer.domain.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainScreenViewModel @Inject constructor(
    val repository: UserPreferencesRepository
) : ViewModel()
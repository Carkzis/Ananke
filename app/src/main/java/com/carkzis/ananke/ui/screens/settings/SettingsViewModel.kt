package com.carkzis.ananke.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carkzis.ananke.utils.CurrentUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    currentUserUseCase: CurrentUserUseCase
): ViewModel() {
    val currentUser = currentUserUseCase().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000L),
        null
    )
}
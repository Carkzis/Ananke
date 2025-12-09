package com.carkzis.ananke.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carkzis.ananke.data.model.User
import com.carkzis.ananke.utils.CurrentUserUseCase
import com.carkzis.ananke.utils.UpdateUsernameUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val currentUserUseCase: CurrentUserUseCase,
    private val updateUsernameUseCase: UpdateUsernameUseCase
): ViewModel() {
    private val _currentUser = MutableStateFlow(User.EMPTY)
    val currentUser: StateFlow<User> = _currentUser

    init {
        viewModelScope.launch {
            _currentUser.value = currentUserUseCase().first()
        }
    }

    fun updateCurrentUsersName(newName: String) {
        val currentUserToUpdate = currentUser.value
        viewModelScope.launch {
            updateUsernameUseCase(currentUserToUpdate, newName)
            _currentUser.update { currentUserUseCase().first() }
        }
    }
}
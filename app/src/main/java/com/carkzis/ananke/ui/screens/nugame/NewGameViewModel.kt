package com.carkzis.ananke.ui.screens.nugame

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carkzis.ananke.data.DEFAULT_TEAM_SIZE
import com.carkzis.ananke.data.model.NewGame
import com.carkzis.ananke.data.model.User
import com.carkzis.ananke.data.repository.GameRepository
import com.carkzis.ananke.ui.screens.nugame.NewGameTextValidator.Companion.descriptionValidator
import com.carkzis.ananke.ui.screens.nugame.NewGameTextValidator.Companion.titleValidator
import com.carkzis.ananke.utils.CurrentUserUseCase
import com.carkzis.ananke.utils.ValidatorResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewGameViewModel @Inject constructor(
    currentUserUseCase: CurrentUserUseCase,
    private val gameRepository: GameRepository
) : ViewModel() {

    val currentUser = currentUserUseCase().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000L),
        User.EMPTY
    )

    private val _gameTitle = MutableStateFlow("")
    val gameTitle = _gameTitle.asStateFlow()

    private val _gameDescription = MutableStateFlow("")
    val gameDescription = _gameDescription.asStateFlow()

    private val _teamSize = MutableStateFlow(DEFAULT_TEAM_SIZE)
    val teamSize = _teamSize.asStateFlow()

    private val _message = MutableSharedFlow<String>()
    val message = _message.asSharedFlow()

    private val _addGameSuccessEvent = MutableSharedFlow<Boolean>()
    val addGameSuccessEvent = _addGameSuccessEvent.asSharedFlow()

    fun updateGameTitle(title: String) {
        setText(
            title,
            { _gameTitle.value = it },
            titleValidator(minLength = 0)
        )
    }

    fun updateGameDescription(description: String) {
        setText(
            description,
            { _gameDescription.value = it },
            descriptionValidator()
        )
    }

    fun updateTeamSize(teamSize: Int) {
        _teamSize.value = teamSize
    }

    fun addNewGame(newGame: NewGame) {
        viewModelScope.launch {
            if (validateGame(newGame)) {
                try {
                    gameRepository.addNewGame(newGame)
                    _addGameSuccessEvent.emit(true)
                } catch (exception: GameAlreadyExistsException) {
                    _message.emit(exception.message)
                }
            } else {
                _addGameSuccessEvent.emit(false)
            }
        }
    }

    private fun setText(
        updatedText: String,
        onValidatedText: (String) -> Unit,
        textValidator: NewGameTextValidator
    ) {
        val textValidation = textValidator.validateText(updatedText)

        if (textValidation is ValidatorResponse.Fail) {
            viewModelScope.launch {
                _message.emit(textValidation.failureMessage)
            }
        } else {
            onValidatedText(updatedText)
        }
    }

    private suspend fun validateGame(newGame: NewGame): Boolean {
        val titleValidation = titleValidator().validateText(newGame.name)
        val descriptionValidation = descriptionValidator().validateText(newGame.description)

        if (titleValidation is ValidatorResponse.Fail) {
            _message.emit(titleValidation.failureMessage)
            return false
        }

        if (descriptionValidation is ValidatorResponse.Fail) {
            _message.emit(descriptionValidation.failureMessage)
            return false
        }

        return true
    }

}
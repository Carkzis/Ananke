package com.carkzis.ananke.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carkzis.ananke.data.GameRepository
import com.carkzis.ananke.data.NewGame
import com.carkzis.ananke.ui.screens.NewGameConstants.Companion.MAXIMUM_GAME_DESCRIPTION_LENGTH
import com.carkzis.ananke.ui.screens.NewGameConstants.Companion.MAXIMUM_GAME_TITLE_LENGTH
import com.carkzis.ananke.ui.screens.NewGameConstants.Companion.MINIMUM_GAME_DESCRIPTION_LENGTH
import com.carkzis.ananke.ui.screens.NewGameConstants.Companion.MINIMUM_GAME_TITLE_LENGTH
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewGameViewModel @Inject constructor(private val gameRepository: GameRepository) :
    ViewModel() {

    private val _gameTitle = MutableStateFlow("")
    val gameTitle = _gameTitle.asStateFlow()

    private val _gameDescription = MutableStateFlow("")
    val gameDescription = _gameDescription.asStateFlow()

    private val _message = MutableSharedFlow<String>()
    val message = _message.asSharedFlow()

    fun setGameTitle(title: String) {
        val gameTitleValidator = NewGameTextValidator(
            minimumLength = MINIMUM_GAME_TITLE_LENGTH,
            maximumLength = MAXIMUM_GAME_TITLE_LENGTH
        )
        setText(title, _gameTitle, gameTitleValidator) { validatorResponse ->
            validatorResponse.asTitleMessage()
        }
    }

    fun setGameDescription(description: String) {
        val gameDescriptionValidator = NewGameTextValidator(
            minimumLength = MINIMUM_GAME_DESCRIPTION_LENGTH,
            maximumLength = MAXIMUM_GAME_DESCRIPTION_LENGTH
        )
        setText(description, _gameDescription, gameDescriptionValidator) { validatorResponse ->
            validatorResponse.asDescriptionMessage()
        }
    }

    fun addNewGame(newGame: NewGame) {
        viewModelScope.launch {
            gameRepository.addNewGame(newGame)
        }
    }
    
    private fun setText(
        text: String,
        receiver: MutableStateFlow<String>,
        textValidator: NewGameTextValidator,
        messageValidation: (NewGameValidatorResponse) -> String
    ) {
        val textValidation = textValidator.validateText(text)

        if (textValidation != NewGameValidatorResponse.PASS) {
            viewModelScope.launch {
                _message.emit(messageValidation(textValidation))
            }
        } else {
            receiver.value = text
        }
    }
}
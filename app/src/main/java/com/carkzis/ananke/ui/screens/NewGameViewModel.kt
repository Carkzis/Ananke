package com.carkzis.ananke.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewGameViewModel @Inject constructor(private val gameRepository: GameRepository) :
    ViewModel() {

    var gameTitle by mutableStateOf("")
        private set

    var gameDescription by mutableStateOf("")
        private set

    private val _message = MutableSharedFlow<String>()
    val message = _message.asSharedFlow()

    fun updateGameTitle(title: String) {
        val gameTitleValidator = NewGameTextValidator(
            minimumLength = MINIMUM_GAME_TITLE_LENGTH,
            maximumLength = MAXIMUM_GAME_TITLE_LENGTH
        )
        setText(
            title,
            { gameTitle = it },
            gameTitleValidator,
            NewGameValidatorResponse::asTitleMessage
        )
    }

    fun updateGameDescription(description: String) {
        val gameDescriptionValidator = NewGameTextValidator(
            minimumLength = MINIMUM_GAME_DESCRIPTION_LENGTH,
            maximumLength = MAXIMUM_GAME_DESCRIPTION_LENGTH
        )
        setText(
            description,
            { gameDescription = it },
            gameDescriptionValidator,
            NewGameValidatorResponse::asDescriptionMessage
        )
    }

    fun addNewGame(newGame: NewGame) {
        viewModelScope.launch {
            gameRepository.addNewGame(newGame)
        }
    }

    private fun setText(
        updatedText: String,
        onValidatedText: (String) -> Unit,
        textValidator: NewGameTextValidator,
        messageForValidation: (NewGameValidatorResponse) -> String
    ) {
        val textValidation = textValidator.validateText(updatedText)

        if (textValidation != NewGameValidatorResponse.PASS) {
            viewModelScope.launch {
                _message.emit(messageForValidation(textValidation))
            }
        } else {
            onValidatedText(updatedText)
        }
    }
}
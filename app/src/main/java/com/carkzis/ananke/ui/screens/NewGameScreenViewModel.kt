package com.carkzis.ananke.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carkzis.ananke.data.GameRepository
import com.carkzis.ananke.data.NewGame
import com.carkzis.ananke.ui.screens.NewGameScreenConstants.Companion.MAXIMUM_GAME_DESCRIPTION_LENGTH
import com.carkzis.ananke.ui.screens.NewGameScreenConstants.Companion.MAXIMUM_GAME_TITLE_LENGTH
import com.carkzis.ananke.ui.screens.NewGameScreenConstants.Companion.MINIMUM_GAME_DESCRIPTION_LENGTH
import com.carkzis.ananke.ui.screens.NewGameScreenConstants.Companion.MINIMUM_GAME_TITLE_LENGTH
import com.carkzis.ananke.ui.screens.NewGameScreenValidatorResponse.Companion.asDescriptionMessage
import com.carkzis.ananke.ui.screens.NewGameScreenValidatorResponse.Companion.asTitleMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewGameScreenViewModel @Inject constructor(private val gameRepository: GameRepository) :
    ViewModel() {

    private val _gameTitle = MutableStateFlow("")
    val gameTitle = _gameTitle.asStateFlow()

    private val _gameDescription = MutableStateFlow("")
    val gameDescription = _gameDescription.asStateFlow()

    private val _message = MutableSharedFlow<String>()
    val message = _message.asSharedFlow()

    fun setGameTitle(title: String) {
        val gameTitleValidator = NewGameScreenTextValidator(
            minimumLength = MINIMUM_GAME_TITLE_LENGTH,
            maximumLength = MAXIMUM_GAME_TITLE_LENGTH
        )
        setText(title, _gameTitle, gameTitleValidator) { validatorResponse ->
            validatorResponse.asTitleMessage()
        }
    }

    fun setGameDescription(description: String) {
        val gameDescriptionValidator = NewGameScreenTextValidator(
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
        textValidator: NewGameScreenTextValidator,
        messageValidation: (NewGameScreenValidatorResponse) -> String
    ) {
        val textValidation = textValidator.validateText(text)

        if (textValidation != NewGameScreenValidatorResponse.PASS) {
            viewModelScope.launch {
                _message.emit(messageValidation(textValidation))
            }
        } else {
            receiver.value = text
        }
    }
}

enum class NewGameScreenMessage(val message: String) {
    GAME_TITLE_TOO_LONG("The game title must be no more than $MAXIMUM_GAME_TITLE_LENGTH characters."),
    GAME_TITLE_EMPTY("You must enter a game title."),
    GAME_DESCRIPTION_TOO_LONG("The game description must be no more than $MAXIMUM_GAME_DESCRIPTION_LENGTH characters.");
}

class NewGameScreenConstants {
    companion object {
        const val MINIMUM_GAME_TITLE_LENGTH = 1
        const val MINIMUM_GAME_DESCRIPTION_LENGTH = 0
        const val MAXIMUM_GAME_TITLE_LENGTH = 30
        const val MAXIMUM_GAME_DESCRIPTION_LENGTH = 200
    }
}

enum class NewGameScreenValidatorResponse {
    TOO_LONG,
    TOO_SHORT,
    PASS;

    companion object {
        fun NewGameScreenValidatorResponse.asTitleMessage() = when (this) {
            TOO_SHORT -> NewGameScreenMessage.GAME_TITLE_EMPTY.message
            TOO_LONG -> NewGameScreenMessage.GAME_TITLE_TOO_LONG.message
            else -> ""
        }

        fun NewGameScreenValidatorResponse.asDescriptionMessage() = when (this) {
            TOO_LONG -> NewGameScreenMessage.GAME_DESCRIPTION_TOO_LONG.message
            else -> ""
        }
    }
}

class NewGameScreenTextValidator(private val minimumLength: Int, private val maximumLength: Int) {
    fun validateText(text: String): NewGameScreenValidatorResponse {
        return when {
            text.length < minimumLength -> NewGameScreenValidatorResponse.TOO_SHORT
            text.length > maximumLength -> NewGameScreenValidatorResponse.TOO_LONG
            else -> NewGameScreenValidatorResponse.PASS
        }
    }
}
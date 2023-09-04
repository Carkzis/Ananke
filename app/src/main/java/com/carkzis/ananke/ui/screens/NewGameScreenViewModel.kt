package com.carkzis.ananke.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carkzis.ananke.data.GameRepository
import com.carkzis.ananke.data.NewGame
import com.carkzis.ananke.ui.screens.NewGameScreenValidatorResponses.Companion.asDescriptionMessage
import com.carkzis.ananke.ui.screens.NewGameScreenValidatorResponses.Companion.asTitleMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewGameScreenViewModel @Inject constructor(private val gameRepository: GameRepository) : ViewModel() {

    private val _gameTitle = MutableStateFlow("")
    val gameTitle = _gameTitle.asStateFlow()

    private val _gameDescription = MutableStateFlow("")
    val gameDescription = _gameDescription.asStateFlow()

    private val _message = MutableSharedFlow<String>()
    val message = _message.asSharedFlow()

    fun setGameTitle(title: String) {
        val gameTitleValidator = NewGameScreenTextValidator(minimumLength = 1, maximumLength = 30)
        val gameTitleValidation = gameTitleValidator.validateText(title)

        if (gameTitleValidation != NewGameScreenValidatorResponses.PASS) {
            viewModelScope.launch {
                _message.emit(gameTitleValidation.asTitleMessage())
            }
        } else {
            _gameTitle.value = title
        }
    }

    fun setGameDescription(title: String) {
        val gameDescriptionValidator = NewGameScreenTextValidator(minimumLength = 1, maximumLength = 200)
        val gameDescriptionValidation = gameDescriptionValidator.validateText(title)

        if (gameDescriptionValidation != NewGameScreenValidatorResponses.PASS) {
            viewModelScope.launch {
                _message.emit(gameDescriptionValidation.asDescriptionMessage())
            }
        } else {
            _gameDescription.value = title
        }
    }

    fun addNewGame(newGame: NewGame) {
        viewModelScope.launch {
            gameRepository.addNewGame(newGame)
        }
    }
}

enum class NewGameScreenMessages(val message: String) {
    GAME_TITLE_TOO_LONG("The game title must be no more than 30 characters."),
    GAME_TITLE_EMPTY("You must enter a game title."),
    GAME_DESCRIPTION_TOO_LONG("The game description must be no more than 200 characters.");
}

enum class NewGameScreenValidatorResponses {
    TOO_LONG,
    TOO_SHORT,
    PASS;

    companion object {
        fun NewGameScreenValidatorResponses.asTitleMessage() = when(this) {
            TOO_SHORT -> NewGameScreenMessages.GAME_TITLE_EMPTY.message
            TOO_LONG -> NewGameScreenMessages.GAME_TITLE_TOO_LONG.message
            else -> ""
        }

        fun NewGameScreenValidatorResponses.asDescriptionMessage() = when(this) {
            TOO_LONG -> NewGameScreenMessages.GAME_DESCRIPTION_TOO_LONG.message
            else -> ""
        }
    }
}

class NewGameScreenTextValidator(private val minimumLength: Int, private val maximumLength: Int) {
    fun validateText(text: String) : NewGameScreenValidatorResponses {
        return when {
            text.length < minimumLength -> NewGameScreenValidatorResponses.TOO_SHORT
            text.length > maximumLength -> NewGameScreenValidatorResponses.TOO_LONG
            else -> NewGameScreenValidatorResponses.PASS
        }
    }
}
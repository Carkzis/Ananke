package com.carkzis.ananke.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carkzis.ananke.data.GameRepository
import com.carkzis.ananke.data.NewGame
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

    private val _message = MutableSharedFlow<String>()
    val message = _message.asSharedFlow()

    fun setGameTitle(title: String) {
        val gameTitleValidator = NewGameScreenTextValidator(minimumLength = 1, maximumLength = 30)
        val gameTitleValidation = gameTitleValidator.validateText(title)

        if (gameTitleValidation != NewGameScreenValidatorResponses.PASS) {
            viewModelScope.launch {
                _message.emit(gameTitleValidation.message)
            }
        } else {
            _gameTitle.value = title
        }
    }

    fun addNewGame(newGame: NewGame) {
        viewModelScope.launch {
            gameRepository.addNewGame(newGame)
        }
    }
}

enum class NewGameScreenValidatorResponses(val message: String) {
    GAME_TITLE_TOO_LONG("The game title must be no more than 30 characters."),
    GAME_TITLE_EMPTY("You must enter a game title."),
    PASS("")
}

class NewGameScreenTextValidator(private val minimumLength: Int = 1, private val maximumLength: Int = 30) {
    fun validateText(text: String) : NewGameScreenValidatorResponses {
        return when {
            text.length < minimumLength -> NewGameScreenValidatorResponses.GAME_TITLE_EMPTY
            text.length > maximumLength -> NewGameScreenValidatorResponses.GAME_TITLE_TOO_LONG
            else -> NewGameScreenValidatorResponses.PASS
        }
    }
}
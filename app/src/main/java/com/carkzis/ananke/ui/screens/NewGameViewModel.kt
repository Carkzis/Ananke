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
        val titleValidator = titleValidator(minLength = 0)
        setText(
            title,
            { gameTitle = it },
            titleValidator,
            NewGameValidatorResponse::asTitleMessage
        )
    }

    fun updateGameDescription(description: String) {
        val descriptionValidator = NewGameTextValidator(listOf { text -> if (text.length > MAXIMUM_GAME_TITLE_LENGTH) NewGameValidatorResponse.TOO_LONG else NewGameValidatorResponse.PASS })
        setText(
            description,
            { gameDescription = it },
            descriptionValidator,
            NewGameValidatorResponse::asDescriptionMessage
        )
    }

    fun addNewGame(newGame: NewGame) {
        viewModelScope.launch {
            if (validateGame(newGame)) {
                gameRepository.addNewGame(newGame)
            }
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

    private suspend fun validateGame(newGame: NewGame): Boolean {
        val descriptionValidator =
            NewGameTextValidator(listOf { text -> if (text.length > MAXIMUM_GAME_TITLE_LENGTH) NewGameValidatorResponse.TOO_LONG else NewGameValidatorResponse.PASS })

        val titleValidation = titleValidator().validateText(newGame.name)
        val descriptionValidation = descriptionValidator.validateText(newGame.description)

        when {
            titleValidation != NewGameValidatorResponse.PASS -> {
                _message.emit(titleValidation.asTitleMessage())
                return false
            }
            descriptionValidation != NewGameValidatorResponse.PASS -> {
                _message.emit(descriptionValidation.asDescriptionMessage())
                return false
            }
        }

        return true
    }

    private fun titleValidator(minLength: Int = MINIMUM_GAME_TITLE_LENGTH, maxLength: Int = MAXIMUM_GAME_TITLE_LENGTH) = NewGameTextValidator(listOf(
        { text -> if (text.isEmpty()) NewGameValidatorResponse.EMPTY else NewGameValidatorResponse.PASS },
        { text -> if (text.length < minLength) NewGameValidatorResponse.TOO_SHORT else NewGameValidatorResponse.PASS },
        { text -> if (text.length > maxLength) NewGameValidatorResponse.TOO_LONG else NewGameValidatorResponse.PASS }
    ))

}
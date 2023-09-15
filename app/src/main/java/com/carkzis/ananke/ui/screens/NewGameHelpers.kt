package com.carkzis.ananke.ui.screens

import com.carkzis.ananke.ui.screens.NewGameConstants.Companion.MAXIMUM_GAME_DESCRIPTION_LENGTH
import com.carkzis.ananke.ui.screens.NewGameConstants.Companion.MAXIMUM_GAME_TITLE_LENGTH

class NewGameConstants {
    companion object {
        const val MINIMUM_GAME_TITLE_LENGTH = 0
        const val MINIMUM_GAME_DESCRIPTION_LENGTH = 0
        const val MAXIMUM_GAME_TITLE_LENGTH = 30
        const val MAXIMUM_GAME_DESCRIPTION_LENGTH = 200
    }
}

enum class NewGameMessage(val message: String) {
    GAME_TITLE_TOO_LONG("The game title must be no more than $MAXIMUM_GAME_TITLE_LENGTH characters."),
    GAME_TITLE_EMPTY("You must enter a game title."),
    GAME_DESCRIPTION_TOO_LONG("The game description must be no more than $MAXIMUM_GAME_DESCRIPTION_LENGTH characters.");
}

class NewGameTextValidator(private val minimumLength: Int, private val maximumLength: Int) {
    fun validateText(text: String): NewGameValidatorResponse {
        return when {
            text.length < minimumLength -> NewGameValidatorResponse.TOO_SHORT
            text.length > maximumLength -> NewGameValidatorResponse.TOO_LONG
            else -> NewGameValidatorResponse.PASS
        }
    }
}

enum class NewGameValidatorResponse {
    TOO_LONG,
    TOO_SHORT,
    PASS;
}

fun NewGameValidatorResponse.asTitleMessage() = when (this) {
    NewGameValidatorResponse.TOO_SHORT -> NewGameMessage.GAME_TITLE_EMPTY.message
    NewGameValidatorResponse.TOO_LONG -> NewGameMessage.GAME_TITLE_TOO_LONG.message
    else -> throw IllegalStateException("There is only a title message if title is too short or too long.")
}

fun NewGameValidatorResponse.asDescriptionMessage() = when (this) {
    NewGameValidatorResponse.TOO_LONG -> NewGameMessage.GAME_DESCRIPTION_TOO_LONG.message
    else -> throw IllegalStateException("There is only a description message if title is too long.")
}
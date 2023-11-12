package com.carkzis.ananke.ui.screens

import com.carkzis.ananke.ui.screens.NewGameConstants.Companion.MAXIMUM_GAME_DESCRIPTION_LENGTH
import com.carkzis.ananke.ui.screens.NewGameConstants.Companion.MAXIMUM_GAME_TITLE_LENGTH
import com.carkzis.ananke.ui.screens.NewGameConstants.Companion.MINIMUM_GAME_TITLE_LENGTH

class NewGameConstants {
    companion object {
        const val MINIMUM_GAME_TITLE_LENGTH = 5
        const val MINIMUM_GAME_DESCRIPTION_LENGTH = 0
        const val MAXIMUM_GAME_TITLE_LENGTH = 30
        const val MAXIMUM_GAME_DESCRIPTION_LENGTH = 200
    }
}

enum class NewGameMessage(val message: String) {
    GAME_TITLE_TOO_SHORT("The game title must be at least $MINIMUM_GAME_TITLE_LENGTH characters."),
    GAME_TITLE_TOO_LONG("The game title must be no more than $MAXIMUM_GAME_TITLE_LENGTH characters."),
    GAME_TITLE_EMPTY("You must enter a game title."),
    GAME_DESCRIPTION_TOO_LONG("The game description must be no more than $MAXIMUM_GAME_DESCRIPTION_LENGTH characters.");
}

enum class NewGameValidatorResponse(val message: String = "") {
    TITLE_EMPTY("You must enter a game title."),
    TITLE_TOO_LONG("The game title must be no more than $MAXIMUM_GAME_TITLE_LENGTH characters."),
    TITLE_TOO_SHORT("The game title must be at least $MINIMUM_GAME_TITLE_LENGTH characters."),
    DESCRIPTION_TOO_LONG("The game description must be no more than $MAXIMUM_GAME_DESCRIPTION_LENGTH characters."),
    PASS;
}
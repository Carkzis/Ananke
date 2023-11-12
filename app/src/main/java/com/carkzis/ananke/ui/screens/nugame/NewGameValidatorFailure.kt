package com.carkzis.ananke.ui.screens.nugame

enum class NewGameValidatorFailure(val message: String) {
    TITLE_EMPTY("You must enter a game title."),
    TITLE_TOO_LONG("The game title must be no more than ${NewGameConstants.MAXIMUM_GAME_TITLE_LENGTH} characters."),
    TITLE_TOO_SHORT("The game title must be at least ${NewGameConstants.MINIMUM_GAME_TITLE_LENGTH} characters."),
    DESCRIPTION_TOO_LONG("The game description must be no more than ${NewGameConstants.MAXIMUM_GAME_DESCRIPTION_LENGTH} characters.")
}
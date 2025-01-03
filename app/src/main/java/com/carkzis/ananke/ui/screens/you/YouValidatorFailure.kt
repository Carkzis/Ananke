package com.carkzis.ananke.ui.screens.you

enum class YouValidatorFailure(val message: String) {
    NAME_TOO_SHORT("The character name must be at least ${YouConstants.MINIMUM_CHARACTER_NAME_LENGTH} characters."),
    NAME_TOO_LONG("The character name must be no more than ${YouConstants.MAXIMUM_CHARACTER_NAME_LENGTH} characters.")
}
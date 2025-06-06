package com.carkzis.ananke.ui.screens.you

class CharacterNamingException : Throwable() {
    override val message = "Attempts to create unique character name failed."
}

class CharacterNameTakenException : Throwable() {
    override val message = "Character name already taken."
}

class CharacterDoesNotExistException : Throwable() {
    override val message = "Cannot update a character that does not exist."
}

class CharacterNotInEditModeException : Throwable() {
    override val message = "You cannot edit a character that is not in edit mode."
}

class CharacterAlreadyExistsForUserException : Throwable() {
    override val message = "A user cannot have more than one character for a game."
}
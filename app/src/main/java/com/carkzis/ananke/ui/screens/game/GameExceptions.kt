package com.carkzis.ananke.ui.screens.game

class EnterGameFailedException : Throwable() {
    override val message = "Failed to enter game."
}

class ExitGameFailedException : Throwable() {
    override val message = "Failed to exit game."
}

class InvalidGameException : Throwable() {
    override val message = "Game is invalid."
}

class GameDoesNotExistException : Throwable() {
    override val message = "Current game does not exist in database."
}

class CreatorIdDoesNotMatchException : Throwable() {
    override val message = "You cannot delete a game that you did not create."
}
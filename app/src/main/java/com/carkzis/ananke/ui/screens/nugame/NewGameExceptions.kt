package com.carkzis.ananke.ui.screens.nugame

class GameAlreadyExistsException : Throwable() {
    override val message = "There is already a game with the same name!"
}

class EnterGameFailedException : Throwable() {
    override val message = "Failed to enter game."
}

class ExitGameFailedException : Throwable() {
    override val message = "Failed to exit game."
}
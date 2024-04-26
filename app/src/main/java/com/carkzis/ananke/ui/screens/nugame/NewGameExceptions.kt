package com.carkzis.ananke.ui.screens.nugame

class GameAlreadyExistsException : Throwable() {
    override val message = "There is already a game with the same name!"
}

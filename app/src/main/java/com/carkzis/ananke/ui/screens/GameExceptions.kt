package com.carkzis.ananke.ui.screens

class EnterGameFailedException : Throwable() {
    override val message = "Failed to enter game."
}

class ExitGameFailedException : Throwable() {
    override val message = "Failed to exit game."
}

class InvalidGameException : Throwable() {
    override val message = "Game is invalid."
}


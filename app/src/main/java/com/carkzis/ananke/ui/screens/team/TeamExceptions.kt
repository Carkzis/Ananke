package com.carkzis.ananke.ui.screens.team

class UserAlreadyExistsException : Throwable() {
    override val message = "A user already exists for that ID."
}
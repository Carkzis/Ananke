package com.carkzis.ananke.ui.screens.you

class CharacterNamingException : Throwable() {
    override val message = "Attempts to create unique character name failed."
}
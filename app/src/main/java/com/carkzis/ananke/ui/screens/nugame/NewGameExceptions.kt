package com.carkzis.ananke.ui.screens.nugame

import android.database.sqlite.SQLiteConstraintException

class GameAlreadyExistsException : Throwable() {
    override val message = "There is already a game with the same name!"
}
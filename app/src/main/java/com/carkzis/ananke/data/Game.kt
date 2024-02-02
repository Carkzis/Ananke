package com.carkzis.ananke.data

data class Game(
    val id: String,
    val name: String,
    val description: String
)

fun Game.toCurrentGame() = CurrentGame(
    id = id
)
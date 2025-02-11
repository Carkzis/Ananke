package com.carkzis.ananke.data.model

data class Game(
    val id: String,
    val name: String,
    val description: String
)

fun Game.toCurrentGame() = CurrentGame(
    id = id,
    name = name,
    description = description
)
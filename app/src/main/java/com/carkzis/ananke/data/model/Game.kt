package com.carkzis.ananke.data.model

data class Game(
    val id: String,
    val name: String,
    val description: String,
    val creatorId: String,
    val teamSize: Int,
)

data class GameWithPlayerCount(
    val game: Game,
    val playerCount: Int
)

fun Game.toCurrentGame() = CurrentGame(
    id = id,
    name = name,
    description = description,
    creatorId = creatorId,
    teamSize = teamSize
)

package com.carkzis.ananke.data

data class Game(
    val id: String,
    val name: String,
    val description: String
)

fun Game.toEntity() = GameEntity(
    id = id,
    name = name,
    description = description
)

/**
 * TODO: This needs removing once the real database has been implemented.
 */
fun dummyGames() = listOf(
    Game("abc", "My First Game", "It is the first one."),
    Game("def", "My Second Game", "It is the second one."),
    Game("ghi", "My Third Game", "It is the third one.")
)
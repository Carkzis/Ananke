package com.example.ananke.data

data class GameData(
    val id: String,
    val name: String,
    val description: String
)

/**
 * TODO: This needs removing once the real database has been implemented.
 */
fun dummyGameData() = listOf(
    GameData("abc", "My First Game", "It is the first one."),
    GameData("def", "My Second Game", "It is the second one."),
    GameData("ghi", "My Third Game", "It is the third one.")
)
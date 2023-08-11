package com.carkzis.ananke.data

data class NewGame(
    val name: String,
    val description: String
)

fun NewGame.toEntity() = GameEntity(
    name = name,
    description = description
)
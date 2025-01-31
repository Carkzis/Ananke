package com.carkzis.ananke.data.model

import com.carkzis.ananke.data.database.GameEntity

data class NewGame(
    val name: String,
    val description: String
)

fun NewGame.toEntity() = GameEntity(
    name = name,
    description = description
)
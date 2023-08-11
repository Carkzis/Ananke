package com.carkzis.ananke.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "games"
)
data class GameEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val description: String
)

fun GameEntity.toDomain() = Game(
    id = id,
    name = name,
    description = description
)
package com.carkzis.ananke.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "games"
)
data class GameEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val name: String,
    val description: String
)

fun GameEntity.toDomain() = Game(
    id = id.toString(),
    name = name,
    description = description
)
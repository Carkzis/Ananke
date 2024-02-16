package com.carkzis.ananke.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "games",
    indices = [Index(value = ["name"], unique = true)]
)
data class GameEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val name: String,
    val description: String
)

fun GameEntity.toDomainListing() = Game(
    id = id.toString(),
    name = name,
    description = description
)

fun GameEntity.toDomainCurrent() = CurrentGame(
    id = id.toString(),
    name = name,
    description = description
)
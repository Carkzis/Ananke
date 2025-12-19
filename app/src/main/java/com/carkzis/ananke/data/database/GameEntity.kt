package com.carkzis.ananke.data.database

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.carkzis.ananke.data.DEFAULT_TEAM_SIZE
import com.carkzis.ananke.data.model.CurrentGame
import com.carkzis.ananke.data.model.Game

@Entity(
    tableName = "games",
    indices = [Index(value = ["name"], unique = true)]
)
data class GameEntity(
    @PrimaryKey(autoGenerate = true)
    val gameId: Long = 0L,
    val name: String,
    val description: String,
    val creatorId: Long,
    val teamSize: Int = DEFAULT_TEAM_SIZE
)

fun GameEntity.toDomainListing() = Game(
    id = gameId.toString(),
    name = name,
    description = description,
    creatorId = creatorId.toString(),
    teamSize = teamSize
)

fun GameEntity.toDomainCurrent() = CurrentGame(
    id = gameId.toString(),
    name = name,
    description = description,
    creatorId = creatorId.toString(),
    teamSize = teamSize
)
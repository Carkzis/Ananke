package com.carkzis.ananke.data.model

import com.carkzis.ananke.data.DEFAULT_TEAM_SIZE
import com.carkzis.ananke.data.database.GameEntity

data class NewGame(
    val name: String,
    val description: String,
    val creatorId: Long,
    val teamSize: Int = DEFAULT_TEAM_SIZE
)

fun NewGame.toEntity() = GameEntity(
    name = name,
    description = description,
    creatorId = creatorId,
    teamSize = teamSize
)
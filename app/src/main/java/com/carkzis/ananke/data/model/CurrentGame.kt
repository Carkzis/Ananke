package com.carkzis.ananke.data.model

import com.carkzis.ananke.data.DEFAULT_TEAM_SIZE

data class CurrentGame(
    val id: String,
    val name: String = "",
    val description: String = "",
    val creatorId: String = "-1",
    val teamSize: Int = DEFAULT_TEAM_SIZE
) {
    companion object {
        val EMPTY = CurrentGame("-1")
    }
}

fun CurrentGame.toGame() = Game(
    this.id,
    this.name,
    this.description,
    this.creatorId,
    this.teamSize
)
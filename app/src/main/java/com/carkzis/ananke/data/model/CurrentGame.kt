package com.carkzis.ananke.data.model

data class CurrentGame(
    val id: String,
    val name: String = "",
    val description: String = "",
    val creatorId: String = "-1"
) {
    companion object {
        val EMPTY = CurrentGame("-1")
    }
}

fun CurrentGame.toGame() = Game(
    this.id,
    this.name,
    this.description,
    this.creatorId
)
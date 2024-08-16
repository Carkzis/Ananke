package com.carkzis.ananke.data

data class CurrentGame(
    val id: String,
    val name: String = "",
    val description: String = ""
) {
    companion object {
        val EMPTY = CurrentGame("-1")
    }
}

fun CurrentGame.toGame() = Game(
    this.id,
    this.name,
    this.description
)
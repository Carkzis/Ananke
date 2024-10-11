package com.carkzis.ananke.data.model

data class GameCharacter(
    val id: String,
    val userName: String,
    val character: String,
    val bio: String
) {
    companion object {
        val EMPTY = GameCharacter(
            "-1",
            "",
            "",
            ""
        )
    }
}
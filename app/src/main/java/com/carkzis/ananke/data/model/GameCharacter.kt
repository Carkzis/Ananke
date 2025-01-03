package com.carkzis.ananke.data.model

import com.carkzis.ananke.data.database.CharacterEntity

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

fun GameCharacter.toCharacterEntity(userId: Long = 0L, gameId: Long = 0L): CharacterEntity {
    return CharacterEntity(
        characterId = id.toLong(),
        characterName = character,
        userOwnerId = userId,
        gameOwnerId = gameId,
        characterBio = bio
    )
}
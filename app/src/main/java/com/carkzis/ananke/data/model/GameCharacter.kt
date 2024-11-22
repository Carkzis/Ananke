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

fun GameCharacter.toCharacterEntity(): CharacterEntity {
    return CharacterEntity(
        characterId = id.toLong(),
        characterName = character,
        characterBio = bio
    )
}
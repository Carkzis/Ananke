package com.carkzis.ananke.data.model

import com.carkzis.ananke.data.database.CharacterEntity
import com.carkzis.ananke.utils.RandomCharacterNameGenerator
import com.carkzis.ananke.utils.CharacterNameGenerator

data class NewCharacter(
    val userId: Long,
    val gameId: Long
)

fun createCharacterEntity(
    characterNameGenerator: CharacterNameGenerator = RandomCharacterNameGenerator,
    userOwnerId: Long,
    gameOwnerId: Long
) = CharacterEntity(
    characterName = characterNameGenerator.generateCharacterName(),
    userOwnerId = userOwnerId,
    gameOwnerId = gameOwnerId,
    characterBio = ""
)

package com.carkzis.ananke.data.model

import com.carkzis.ananke.data.database.CharacterEntity
import com.carkzis.ananke.utils.RandomUserNameGenerator
import kotlin.random.Random

data class NewCharacter(
    val userId: Long,
    val gameId: Long
)

fun NewCharacter.toEntity() = CharacterEntity(
    userId = userId,
    characterName = RandomUserNameGenerator.generateRandomUserName(),
    characterBio = ""
)

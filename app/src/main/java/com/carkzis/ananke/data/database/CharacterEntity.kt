package com.carkzis.ananke.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.carkzis.ananke.data.model.GameCharacter

@Entity(
    tableName = "characters"
)
data class CharacterEntity(
    @PrimaryKey(autoGenerate = true)
    val characterId: Long = 0L,
    val userId: Long,
    val characterName: String,
    val characterBio: String
)

fun CharacterEntity.toCharacter() = GameCharacter(
    id = characterId.toString(),
    userName = "",
    character = characterName,
    bio = characterBio
)
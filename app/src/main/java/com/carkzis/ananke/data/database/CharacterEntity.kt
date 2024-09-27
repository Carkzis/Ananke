package com.carkzis.ananke.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "characters"
)
data class CharacterEntity(
    @PrimaryKey
    val characterId: Long,
    val userId: Long,
    val characterName: String,
    val characterBio: String
)
package com.carkzis.ananke.data.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface YouDao {
    @Upsert
    suspend fun insertOrUpdateCharacter(character: CharacterEntity)

    @Query(value = "SELECT * FROM characters WHERE characterId = :characterId")
    fun getCharacterForId(characterId: Long): Flow<CharacterEntity?>
}
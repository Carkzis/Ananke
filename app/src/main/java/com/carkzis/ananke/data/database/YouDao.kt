package com.carkzis.ananke.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface YouDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertCharacter(character: CharacterEntity)

    @Query(value = "SELECT * FROM characters WHERE characterId = :characterId")
    fun getCharacterForId(characterId: Long): Flow<CharacterEntity>
}
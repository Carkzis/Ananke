package com.carkzis.ananke.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface YouDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCharacter(character: CharacterEntity)

    @Update
    suspend fun updateCharacter(character: CharacterEntity)

    @Transaction
    @Query(value = """
            SELECT * FROM characters
            WHERE userOwnerId = :userId
        """
    )
    fun getCharactersForUserId(userId: Long): Flow<List<CharacterEntity>>

    @Transaction
    @Query(
        value = """
            SELECT * FROM users
            INNER JOIN characters ON users.userId = characters.userOwnerId
            WHERE characters.characterId = :characterId
        """
    )
    fun getUserForCharacterId(characterId: Long): Flow<UserEntityWithCharacters>

    @Transaction
    @Query(
        value = """
            SELECT * FROM characters
            INNER JOIN games ON characters.gameOwnerId = games.gameId
            WHERE games.gameId = :gameId
        """
    )
    fun getCharactersForGameId(gameId: Long): Flow<GameEntityWithCharacters?>
}
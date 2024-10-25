package com.carkzis.ananke.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface YouDao {
    @Upsert
    suspend fun insertOrUpdateCharacter(character: CharacterEntity)

    @Query(value = "SELECT * FROM characters WHERE characterId = :characterId")
    fun getCharacterForId(characterId: Long): Flow<CharacterEntity?>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertOrIgnoreCharacterUserCrossRefEntities(characterUserCrossRef: UserCharacterCrossRef)

    @Transaction
    @Query(
        value = """
            SELECT * FROM users
            INNER JOIN UserCharacterCrossRef ON users.userId = UserCharacterCrossRef.userId
            INNER JOIN characters ON characters.characterId = UserCharacterCrossRef.characterId
            WHERE characters.characterId = :characterId
        """
    )
    fun getUserForCharacterId(characterId: Long): Flow<UserEntityWithCharacters>

}
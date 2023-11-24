package com.carkzis.ananke.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {

    @Query(value = "SELECT * FROM games ORDER BY id DESC")
    fun getGames(): Flow<List<GameEntity>>

    @Upsert
    suspend fun upsertGames(gameEntities: List<GameEntity>)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertGame(game: GameEntity)
}
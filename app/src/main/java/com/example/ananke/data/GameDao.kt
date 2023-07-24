package com.example.ananke.data

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {

    @Query(value = "SELECT * FROM games ORDER BY id DESC")
    fun getGames(): Flow<List<GameEntity>>

    @Upsert
    suspend fun upsertGames(gameEntities: List<GameEntity>)
}
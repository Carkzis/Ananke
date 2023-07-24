package com.example.ananke.data

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {

    @Query(value = "SELECT * FROM games")
    fun getGames(): Flow<List<GameEntity>>
}
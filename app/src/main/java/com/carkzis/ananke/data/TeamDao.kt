package com.carkzis.ananke.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface TeamDao {
    @Query(value = "SELECT * FROM users")
    fun getTeamMembers(): Flow<List<UserEntity>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertTeamMember(teamMember: UserEntity)

    @Transaction
    @Query(value = "SELECT * FROM users INNER JOIN games WHERE games.gameId = :gameId")
    fun getTeamMembersWithGames(gameId: Long): Flow<List<UserEntityWithGames>>
}
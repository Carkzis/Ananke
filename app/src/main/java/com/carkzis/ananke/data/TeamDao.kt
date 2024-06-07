package com.carkzis.ananke.data

import androidx.room.Insert
import androidx.room.OnConflictStrategy
import kotlinx.coroutines.flow.Flow

interface TeamDao {
    fun getTeamMembers(gameId: Long): Flow<List<UserEntity>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertTeamMember(teamMember: UserEntity)
}
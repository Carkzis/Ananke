package com.carkzis.ananke.data.database

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

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertOrIgnoreUserGameCrossRefEntities(
        userGameCrossReferences: List<UserGameCrossRef>
    )

    @Transaction
    @Query(
        value = """
            SELECT * FROM users 
            INNER JOIN UserGameCrossRef ON users.userId = UserGameCrossRef.userId
            INNER JOIN games ON games.gameId = UserGameCrossRef.gameId
            WHERE games.gameId = :gameId
        """,
    )
    fun getTeamMembersForGame(gameId: Long): Flow<List<UserEntityWithGames>>
}
package com.carkzis.ananke.data.database

import kotlinx.coroutines.flow.Flow

interface AnankeDataStore {
    suspend fun currentGameId(): Flow<String?>

    suspend fun setCurrentGameId(gameId: String)

    suspend fun removeCurrentGameId()

    suspend fun currentUserId(): Flow<String?>

    suspend fun setCurrentUserId(userId: String)
}
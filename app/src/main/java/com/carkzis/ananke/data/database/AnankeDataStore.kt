package com.carkzis.ananke.data.database

import kotlinx.coroutines.flow.Flow

interface AnankeDataStore {

    fun currentGameId(): Flow<String?>

    suspend fun setCurrentGameId(gameId: String)

    suspend fun removeCurrentGameId()

    fun currentUserId(): Flow<String?>

    suspend fun setCurrentUserId(userId: String)
}
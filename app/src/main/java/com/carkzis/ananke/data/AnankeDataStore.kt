package com.carkzis.ananke.data

import kotlinx.coroutines.flow.Flow

interface AnankeDataStore {
    suspend fun currentGameId(): Flow<String?>

    suspend fun setCurrentGameId(gameId: String)

    suspend fun removeCurrentGameId()
}
package com.carkzis.ananke.data

import kotlinx.coroutines.flow.Flow

interface DataStoreWrapper {
    val data: Flow<String?>
    suspend fun setCurrentGameId(gameId: String)
}
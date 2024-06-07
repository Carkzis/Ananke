package com.carkzis.ananke.data

import kotlinx.coroutines.flow.Flow

interface TeamRepository {
    suspend fun getUsers(): Flow<List<User>>
}
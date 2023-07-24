package com.example.ananke.data

import kotlinx.coroutines.flow.Flow

interface GameRepository {
    val games: Flow<List<Game>>
}
package com.example.ananke.data

import kotlinx.coroutines.flow.Flow

interface GameRepository {
    val gamesData: Flow<List<GameData>>
}
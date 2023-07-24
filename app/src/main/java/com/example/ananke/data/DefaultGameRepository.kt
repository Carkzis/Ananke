package com.example.ananke.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class DefaultGameRepository @Inject constructor() : GameRepository {
    override val games: Flow<List<Game>>
        get() = flowOf(dummyGames())
}
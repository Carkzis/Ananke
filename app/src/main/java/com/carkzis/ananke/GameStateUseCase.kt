package com.carkzis.ananke

import com.carkzis.ananke.data.CurrentGame
import com.carkzis.ananke.data.GameRepository
import com.carkzis.ananke.ui.screens.GamingState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GameStateUseCase(
    private val gameRepository: GameRepository
) {
    operator fun invoke(): Flow<GamingState> = gameRepository.getCurrentGame().map {
        if (it == CurrentGame.EMPTY) {
            GamingState.OutOfGame
        } else {
            GamingState.InGame(it)
        }
    }
}

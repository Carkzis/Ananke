package com.carkzis.ananke.utils

import com.carkzis.ananke.data.CurrentGame
import com.carkzis.ananke.data.GameRepository
import com.carkzis.ananke.ui.screens.game.GamingState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GameStateUseCase @Inject constructor(
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

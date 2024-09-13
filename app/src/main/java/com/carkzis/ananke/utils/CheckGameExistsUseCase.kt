package com.carkzis.ananke.utils

import com.carkzis.ananke.data.Game
import com.carkzis.ananke.data.GameRepository
import com.carkzis.ananke.ui.screens.team.UserAddedToNonExistentGameException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CheckGameExistsUseCase @Inject constructor(private val gameRepository: GameRepository) {
    operator fun invoke(game: Game): Flow<ValidatorResponse> = gameRepository.getGames().map {
        if (it.contains(game)) {
            ValidatorResponse.Pass
        } else {
            val nonExistentGameMessage = UserAddedToNonExistentGameException(game.name).message
            ValidatorResponse.Fail(nonExistentGameMessage)
        }
    }
}
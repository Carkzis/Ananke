package com.carkzis.ananke.utils

import com.carkzis.ananke.data.model.Game
import com.carkzis.ananke.data.repository.YouRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class DeletableGameUseCase @Inject constructor(private val youRepository: YouRepository) {
    suspend operator fun invoke(game: Game): Boolean {
        val currentUser = youRepository.getCurrentUser().first()
        return game.creatorId == currentUser.id.toString()
    }
}
package com.carkzis.ananke.utils

import com.carkzis.ananke.data.model.CurrentGame
import com.carkzis.ananke.data.model.NewCharacter
import com.carkzis.ananke.data.repository.TeamRepository
import com.carkzis.ananke.data.repository.YouRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class AddCurrentUserToTheirEmptyGameUseCase @Inject constructor(
    private val teamRepository: TeamRepository,
    private val youRepository: YouRepository
) {
    suspend operator fun invoke(currentGame: CurrentGame) {
        val teamMembers = teamRepository.getTeamMembers(currentGame.id.toLong()).first()
        val currentUser = youRepository.getCurrentUser().first()

        if (teamMembers.isEmpty() && currentGame.creatorId.toLong() == currentUser.id) {
            teamRepository.addTeamMember(currentUser, currentGame.id.toLong())
            val newCharacter = NewCharacter(
                userId = currentGame.creatorId.toLong(),
                gameId = currentGame.id.toLong(),
            )
            // TODO: Catch exception here.
            youRepository.addNewCharacter(newCharacter)
        }
    }
}
package com.carkzis.ananke.utils

import com.carkzis.ananke.data.model.NewCharacter
import com.carkzis.ananke.data.model.User
import com.carkzis.ananke.data.repository.TeamRepository
import com.carkzis.ananke.data.repository.YouRepository
import javax.inject.Inject

class AddTeamMemberUseCase @Inject constructor(
    private val teamRepository: TeamRepository,
    private val youRepository: YouRepository
) {
    suspend operator fun invoke(teamMember: User, currentGameId: Long) {
        teamRepository.addTeamMember(teamMember, currentGameId)

        youRepository.addNewCharacter(
            NewCharacter(
                userId = teamMember.id,
                gameId = currentGameId,
            )
        )
    }
}
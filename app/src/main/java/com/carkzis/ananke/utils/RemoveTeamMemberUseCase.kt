package com.carkzis.ananke.utils

import com.carkzis.ananke.data.model.GameCharacter
import com.carkzis.ananke.data.model.User
import com.carkzis.ananke.data.repository.TeamRepository
import com.carkzis.ananke.data.repository.YouRepository
import javax.inject.Inject

class RemoveTeamMemberUseCase @Inject constructor(
    private val teamRepository: TeamRepository,
    private val youRepository: YouRepository
) {
    suspend operator fun invoke(teamMemberForRemoval: User, characterForRemoval: GameCharacter) {
        teamRepository.deleteTeamMember(teamMemberForRemoval)
        youRepository.deleteCharacter(characterForRemoval)
    }
}
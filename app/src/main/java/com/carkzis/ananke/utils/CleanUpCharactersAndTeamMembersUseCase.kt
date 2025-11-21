package com.carkzis.ananke.utils

import com.carkzis.ananke.data.repository.TeamRepository
import com.carkzis.ananke.data.repository.YouRepository
import javax.inject.Inject

class CleanUpCharactersAndTeamMembersUseCase @Inject constructor(
    private val youRepository: YouRepository,
    private val teamRepository: TeamRepository
) {
    suspend operator fun invoke(gameId: Long) {
        youRepository.deleteCharactersForGame(gameId)
        teamRepository.deleteTeamMembersForGame(gameId)
    }
}
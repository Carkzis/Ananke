package com.carkzis.ananke.utils

import com.carkzis.ananke.data.repository.TeamRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class PlayerCountUseCase @Inject constructor(
    private val teamRepository: TeamRepository
) {
    suspend operator fun invoke(gameId: Long): Int {
        val teamMembers = teamRepository.getTeamMembers(gameId).first()
        return teamMembers.size
    }
}
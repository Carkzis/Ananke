package com.carkzis.ananke.utils

import com.carkzis.ananke.data.model.User
import com.carkzis.ananke.data.repository.YouRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class UserCharacterUseCase @Inject constructor(
    private val youRepository: YouRepository
) {
    suspend operator fun invoke(user: User, gameId: Long) =
        youRepository.getCharacterForUser(user, gameId).first()
}
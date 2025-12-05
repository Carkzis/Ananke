package com.carkzis.ananke.utils

import com.carkzis.ananke.data.model.User
import com.carkzis.ananke.data.repository.YouRepository
import javax.inject.Inject

class UpdateUsernameUseCase @Inject constructor(
    private val youRepository: YouRepository
) {
    suspend operator fun invoke(user: User, newUsername: String) {
        youRepository.updateUsername(user, newUsername)
    }
}
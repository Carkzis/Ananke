package com.carkzis.ananke.utils

import com.carkzis.ananke.data.repository.YouRepository
import javax.inject.Inject

class CurrentUserUseCase @Inject constructor(private val youRepository: YouRepository) {
    operator fun invoke() = youRepository.getCurrentUser()
}
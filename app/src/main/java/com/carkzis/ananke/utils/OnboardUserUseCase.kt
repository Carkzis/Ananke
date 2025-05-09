package com.carkzis.ananke.utils

import com.carkzis.ananke.data.repository.YouRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class OnboardUserUseCase @Inject constructor(private val youRepository: YouRepository)  {
    suspend operator fun invoke() = youRepository.getCurrentUser().first()
}
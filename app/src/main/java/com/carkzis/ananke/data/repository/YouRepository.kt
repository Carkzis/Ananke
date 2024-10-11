package com.carkzis.ananke.data.repository

import com.carkzis.ananke.data.model.GameCharacter
import com.carkzis.ananke.data.model.NewCharacter
import kotlinx.coroutines.flow.Flow

interface YouRepository {
    fun getCharacter(characterId: Long): Flow<GameCharacter>
    suspend fun addNewCharacter(character: NewCharacter)
}
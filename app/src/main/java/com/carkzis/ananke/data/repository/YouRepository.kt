package com.carkzis.ananke.data.repository

import com.carkzis.ananke.data.model.GameCharacter
import com.carkzis.ananke.data.model.NewCharacter
import com.carkzis.ananke.data.model.User
import kotlinx.coroutines.flow.Flow

interface YouRepository {
    fun getCharacterForUser(user: User): Flow<GameCharacter>
    suspend fun addNewCharacter(character: NewCharacter)
}
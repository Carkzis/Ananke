package com.carkzis.ananke.data.repository

import com.carkzis.ananke.data.model.Character
import com.carkzis.ananke.data.model.NewCharacter
import kotlinx.coroutines.flow.Flow

interface YouRepository {
    fun getCharacter(characterId: Long): Flow<Character>
    fun addNewCharacter(character: NewCharacter)
}
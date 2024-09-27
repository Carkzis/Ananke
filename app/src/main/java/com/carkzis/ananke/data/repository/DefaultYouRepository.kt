package com.carkzis.ananke.data.repository

import com.carkzis.ananke.data.database.YouDao
import com.carkzis.ananke.data.model.Character
import com.carkzis.ananke.data.model.NewCharacter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class DefaultYouRepository @Inject constructor(
    private val youDao: YouDao
) : YouRepository {
    override fun getCharacter(characterId: Long): Flow<Character> = flow {}

    override fun addNewCharacter(character: NewCharacter) {}
}
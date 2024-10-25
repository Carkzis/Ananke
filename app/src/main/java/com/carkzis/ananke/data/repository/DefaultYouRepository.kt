package com.carkzis.ananke.data.repository

import com.carkzis.ananke.data.database.YouDao
import com.carkzis.ananke.data.database.toCharacter
import com.carkzis.ananke.data.model.GameCharacter
import com.carkzis.ananke.data.model.NewCharacter
import com.carkzis.ananke.data.model.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DefaultYouRepository @Inject constructor(
    private val youDao: YouDao
) : YouRepository {
    override fun getCharacterForUser(userId: Long): Flow<GameCharacter> = youDao
            .getCharactersForUserId(userId)
            .map {
                // TODO: This will ultimately need to return a character for the current game.
                it.first().toCharacter()
            }

    override suspend fun addNewCharacter(character: NewCharacter) {
        youDao.insertOrUpdateCharacter(character.toEntity())
    }
}
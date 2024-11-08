package com.carkzis.ananke.data.repository

import com.carkzis.ananke.data.database.UserCharacterCrossRef
import com.carkzis.ananke.data.database.YouDao
import com.carkzis.ananke.data.database.toCharacter
import com.carkzis.ananke.data.model.GameCharacter
import com.carkzis.ananke.data.model.NewCharacter
import com.carkzis.ananke.data.model.User
import com.carkzis.ananke.data.model.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DefaultYouRepository @Inject constructor(
    private val youDao: YouDao
) : YouRepository {
    override fun getCharacterForUser(user: User): Flow<GameCharacter> = youDao
            .getCharactersForUserId(user.id)
            .map {
                // TODO: This will ultimately need to return a character for the current game.
                it.first().toCharacter(userName = user.name)
            }

    override suspend fun addNewCharacter(character: NewCharacter) {
        val characterEntity = character.toEntity()
        youDao.insertOrUpdateCharacter(characterEntity)
        youDao.insertOrIgnoreCharacterUserCrossRefEntities(UserCharacterCrossRef(characterEntity.characterId, character.userId))
    }
}
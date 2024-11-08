package com.carkzis.ananke.data.repository

import com.carkzis.ananke.data.database.CharacterGameCrossRef
import com.carkzis.ananke.data.database.UserCharacterCrossRef
import com.carkzis.ananke.data.database.YouDao
import com.carkzis.ananke.data.database.toCharacter
import com.carkzis.ananke.data.model.GameCharacter
import com.carkzis.ananke.data.model.NewCharacter
import com.carkzis.ananke.data.model.User
import com.carkzis.ananke.data.model.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DefaultYouRepository @Inject constructor(
    private val youDao: YouDao
) : YouRepository {
    override fun getCharacterForUser(user: User, currentGameId: Long): Flow<GameCharacter> = flow {
        val charactersForUserId = youDao.getCharactersForUserId(user.id).first()
        val charactersForGameId = youDao.getCharactersForGameId(currentGameId).first().characterEntities

        val characterForUserInGame = charactersForUserId.first { character ->
            charactersForGameId.map { it.characterId }.contains(character.characterId)
        }
        emit(
            characterForUserInGame.toCharacter(userName = user.name)
        )
    }

    override suspend fun addNewCharacter(newCharacter: NewCharacter) {
        val characterEntity = newCharacter.toEntity()
        youDao.insertOrUpdateCharacter(characterEntity)
        youDao.insertOrIgnoreUserCharacterCrossRefEntities(UserCharacterCrossRef(characterEntity.characterId, newCharacter.userId))
        youDao.insertOrIgnoreCharacterGameCrossRefEntities(CharacterGameCrossRef(characterEntity.characterId, newCharacter.gameId))
    }
}
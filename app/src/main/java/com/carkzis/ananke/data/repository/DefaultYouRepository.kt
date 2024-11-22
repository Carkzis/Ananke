package com.carkzis.ananke.data.repository

import com.carkzis.ananke.data.database.CharacterGameCrossRef
import com.carkzis.ananke.data.database.UserCharacterCrossRef
import com.carkzis.ananke.data.database.YouDao
import com.carkzis.ananke.data.database.toCharacter
import com.carkzis.ananke.data.model.GameCharacter
import com.carkzis.ananke.data.model.NewCharacter
import com.carkzis.ananke.data.model.User
import com.carkzis.ananke.data.model.createCharacterEntity
import com.carkzis.ananke.data.model.toCharacterEntity
import com.carkzis.ananke.ui.screens.you.CharacterDoesNotExistException
import com.carkzis.ananke.ui.screens.you.CharacterNameTakenException
import com.carkzis.ananke.ui.screens.you.CharacterNamingException
import com.carkzis.ananke.utils.RandomCharacterNameGenerator
import com.carkzis.ananke.utils.CharacterNameGenerator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class DefaultYouRepository @Inject constructor(
    private val youDao: YouDao,
    private val characterNameGenerator: CharacterNameGenerator = RandomCharacterNameGenerator
) : YouRepository {
    override fun getCharacterForUser(user: User, currentGameId: Long): Flow<GameCharacter> = flow {
        val charactersForUserId = youDao.getCharactersForUserId(user.id).first()
        val charactersForGameId = youDao.getCharactersForGameId(currentGameId).first()?.characterEntities ?: listOf()

        val characterForUserInGame = charactersForUserId.first { character ->
            charactersForGameId.map { it.characterId }.contains(character.characterId)
        }
        emit(
            characterForUserInGame.toCharacter(userName = user.name)
        )
    }

    override suspend fun addNewCharacter(newCharacter: NewCharacter) {
        val characterNamesForGameId = youDao.getCharactersForGameId(newCharacter.gameId)
                .first()
                ?.characterEntities
                ?.map {
                    it.characterName
                } ?: listOf()

        for (characterNamingAttempt in 0..10) {
            if (characterNamingAttempt == 10) throw CharacterNamingException()

            val characterEntity = createCharacterEntity(characterNameGenerator)

            if (characterNamesForGameId.contains(characterEntity.characterName)) {
                continue
            } else {
                youDao.insertCharacter(characterEntity)
                youDao.insertOrIgnoreUserCharacterCrossRefEntities(UserCharacterCrossRef(characterEntity.characterId, newCharacter.userId))
                youDao.insertOrIgnoreCharacterGameCrossRefEntities(CharacterGameCrossRef(characterEntity.characterId, newCharacter.gameId))
                break
            }
        }
    }

    override suspend fun updateCharacter(character: GameCharacter, currentGameId: Long) {
        val currentCharacters = youDao.getCharactersForGameId(currentGameId).first()
            ?.characterEntities
        val currentCharacterIds = currentCharacters?.map { it.characterId } ?: listOf()
        val unavailableCharacterNames = currentCharacters?.map { it.characterName } ?: listOf()

        when {
            !currentCharacterIds.contains(character.id.toLong()) -> throw CharacterDoesNotExistException()
            unavailableCharacterNames.contains(character.character) -> throw CharacterNameTakenException()
            else -> youDao.updateCharacter(character.toCharacterEntity())
        }
    }
}
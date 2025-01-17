package com.carkzis.ananke.data.repository

import com.carkzis.ananke.data.database.YouDao
import com.carkzis.ananke.data.database.toCharacter
import com.carkzis.ananke.data.model.GameCharacter
import com.carkzis.ananke.data.model.NewCharacter
import com.carkzis.ananke.data.model.User
import com.carkzis.ananke.data.model.createCharacterEntity
import com.carkzis.ananke.data.model.toCharacterEntity
import com.carkzis.ananke.ui.screens.you.CharacterAlreadyExistsForUserException
import com.carkzis.ananke.ui.screens.you.CharacterDoesNotExistException
import com.carkzis.ananke.ui.screens.you.CharacterNameTakenException
import com.carkzis.ananke.ui.screens.you.CharacterNamingException
import com.carkzis.ananke.utils.CharacterNameGenerator
import com.carkzis.ananke.utils.RandomCharacterNameGenerator
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DefaultYouRepository @Inject constructor(
    private val youDao: YouDao,
    private val characterNameGenerator: CharacterNameGenerator = RandomCharacterNameGenerator
) : YouRepository {

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getCharacterForUser(user: User, currentGameId: Long): Flow<GameCharacter> =
        youDao.getCharactersForUserId(user.id).flatMapLatest { charactersForUserId ->
            youDao.getCharactersForGameId(currentGameId).map { charactersForGameId ->
                if (charactersForUserId.isEmpty()) {
                    return@map GameCharacter.EMPTY
                }

                val characterForUserInGame = charactersForUserId.first { character ->
                    charactersForGameId?.characterEntities?.map { it.characterId }?.contains(character.characterId) ?: false
                }

                return@map characterForUserInGame.toCharacter(userName = user.name)
            }
        }

    override suspend fun addNewCharacter(newCharacter: NewCharacter) {
        val charactersForGameId = youDao.getCharactersForGameId(newCharacter.gameId)
                .first()
                ?.characterEntities

        val userIdsForGameId = charactersForGameId?.map {
            it.userOwnerId
        } ?: listOf()

        if (userIdsForGameId.contains(newCharacter.userId)) {
            throw CharacterAlreadyExistsForUserException()
        }

        val characterNamesForGameId = charactersForGameId?.map {
            it.characterName
        } ?: listOf()

        for (characterNamingAttempt in 0..10) {
            if (characterNamingAttempt == 10) throw CharacterNamingException()

            val characterEntity = createCharacterEntity(characterNameGenerator, newCharacter.userId, newCharacter.gameId)

            if (characterNamesForGameId.contains(characterEntity.characterName)) {
                continue
            } else {
                youDao.insertCharacter(characterEntity)
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
            else -> {
                val userId = youDao.getUserForCharacterId(character.id.toLong()).first().userEntity.userId
                youDao.updateCharacter(character.toCharacterEntity(userId, currentGameId))
            }
        }
    }
}
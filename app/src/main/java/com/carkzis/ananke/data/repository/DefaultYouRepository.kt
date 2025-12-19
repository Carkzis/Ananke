package com.carkzis.ananke.data.repository

import com.carkzis.ananke.data.database.AnankeDataStore
import com.carkzis.ananke.data.database.UserEntity
import com.carkzis.ananke.data.database.YouDao
import com.carkzis.ananke.data.database.toCharacter
import com.carkzis.ananke.data.database.toDomain
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
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import kotlin.math.abs

class DefaultYouRepository @Inject constructor(
    private val youDao: YouDao,
    private val characterNameGenerator: CharacterNameGenerator = RandomCharacterNameGenerator,
    private val anankeDataStore: AnankeDataStore? = null
) : YouRepository {

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getCharacterForUser(user: User, currentGameId: Long): Flow<GameCharacter> =
        youDao.getCharactersForUserId(user.id).flatMapLatest { charactersForUserId ->
            youDao.getCharactersForGameId(currentGameId).map { charactersForGameId ->
                if (charactersForUserId.isEmpty()) {
                    return@map GameCharacter.EMPTY
                }

                val characterForUserInGame = charactersForUserId.firstOrNull { character ->
                    charactersForGameId?.characterEntities?.map { it.characterId }?.contains(character.characterId) ?: false
                }

                val domainCharacter = characterForUserInGame?.let {
                    characterForUserInGame.toCharacter(userName = user.name)
                } ?: GameCharacter.EMPTY

                return@map domainCharacter
            }
        }

    override fun getCurrentUser(): Flow<User> = flow {
        val currentUserId = anankeDataStore?.currentUserId()?.first()

        if (currentUserId == null) {
            val userId = UUID.randomUUID().mostSignificantBits and Long.MAX_VALUE
            val userName = "User-${abs(UUID.randomUUID().mostSignificantBits).toString().take(5)}"

            val newUser = UserEntity(userId, userName)

            youDao.insertUser(newUser)

            anankeDataStore?.setCurrentUserId(userId.toString())

            emit(newUser.toDomain())
        } else {
            val user = youDao.getUserForUserId(currentUserId.toLong()).first()
            user?.let { emit(it.toDomain()) }
        }
    }

    override suspend fun updateUsername(user: User, newName: String) {
        val updatedUser = UserEntity(
            userId = user.id,
            username = newName
        )
        youDao.insertUser(updatedUser)
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

    override suspend fun updateCharacter(character: GameCharacter, currentGameId: Long, formerGameCharacter: GameCharacter) {
        val currentCharacters = youDao.getCharactersForGameId(currentGameId).first()
            ?.characterEntities
        val currentCharacterIds = currentCharacters?.map { it.characterId } ?: listOf()
        val unavailableCharacterNames = currentCharacters?.map { it.characterName } ?: listOf()

        when {
            !currentCharacterIds.contains(character.id.toLong()) -> throw CharacterDoesNotExistException()
            unavailableCharacterNames.contains(character.character) && character.character != formerGameCharacter.character -> throw CharacterNameTakenException()
            else -> {
                val userId = youDao.getUserForCharacterId(character.id.toLong()).first().userEntity.userId
                youDao.updateCharacter(character.toCharacterEntity(userId, currentGameId))
            }
        }
    }

    override suspend fun deleteCharactersForGame(gameId: Long) {
        youDao.deleteCharactersForGameId(gameId)
    }

    override suspend fun deleteCharacter(character: GameCharacter) {
        youDao.deleteCharacter(character.id.toLong())
    }
}
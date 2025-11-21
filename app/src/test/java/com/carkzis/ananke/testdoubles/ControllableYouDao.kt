package com.carkzis.ananke.testdoubles

import com.carkzis.ananke.data.database.CharacterEntity
import com.carkzis.ananke.data.database.GameEntityWithCharacters
import com.carkzis.ananke.data.database.UserEntity
import com.carkzis.ananke.data.database.UserEntityWithCharacters
import com.carkzis.ananke.data.database.YouDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class ControllableYouDao: YouDao {
    var characters = MutableStateFlow(listOf<CharacterEntity>())

    private val listOfUsers = dummyUserEntities.toMutableList()
    private var idCounter = 0L

    override suspend fun insertCharacter(character: CharacterEntity) {
        val characterIdIncreased = character.copy(characterId = idCounter)
        characters.update { previousValues ->
            (listOf(characterIdIncreased) + previousValues)
                .distinctBy(CharacterEntity::characterId)
                .sortedWith(idDescending())
        }
        idCounter++
    }

    override suspend fun updateCharacter(character: CharacterEntity) {
        characters.update { previousValues ->
            previousValues.map { currentCharacter ->
                if (currentCharacter.characterId == character.characterId) character else currentCharacter
            }
        }
    }

    override fun getUserForUserId(userId: Long): Flow<UserEntity?> = listOfUsers.filter {
        it.userId == userId
    }.let { user ->
        flow {
            emit(user.firstOrNull())
        }
    }

    override suspend fun insertUser(user: UserEntity) {
        listOfUsers.add(user)
    }

    override fun getCharactersForUserId(userId: Long): Flow<List<CharacterEntity>> = flow {
        val charactersForUserId = characters.value.filter {
            it.userOwnerId == userId
        }

        emit(
            charactersForUserId
        )
    }


    override fun getUserForCharacterId(characterId: Long): Flow<UserEntityWithCharacters> = flow {
        val charactersForCharacterId = characters.value.filter {
            it.characterId == characterId
        }

        val userIdForCharacter = charactersForCharacterId.first().userOwnerId
        val userForUserId = listOfUsers.first {
            it.userId == userIdForCharacter
        }

        emit(
            UserEntityWithCharacters(userForUserId, charactersForCharacterId)
        )
    }

    override fun getCharactersForGameId(gameId: Long): Flow<GameEntityWithCharacters?> = flow {
        val gameForCharacters = dummyGameEntities.firstOrNull {
            it.gameId == gameId
        }

        val charactersForGame = characters.value.filter {
            it.gameOwnerId == gameId
        }

        emit(
            gameForCharacters?.let { GameEntityWithCharacters(it, charactersForGame) }
        )
    }

    override suspend fun deleteCharactersForGameId(gameId: Long) {
        characters.value = characters.value.filter {
            it.gameOwnerId != gameId
        }
    }

    private fun idDescending() = compareBy(CharacterEntity::characterId).reversed()
}
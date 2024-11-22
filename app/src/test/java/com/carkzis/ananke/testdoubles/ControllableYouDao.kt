package com.carkzis.ananke.testdoubles

import com.carkzis.ananke.data.database.CharacterEntity
import com.carkzis.ananke.data.database.CharacterGameCrossRef
import com.carkzis.ananke.data.database.GameEntityWithCharacters
import com.carkzis.ananke.data.database.UserCharacterCrossRef
import com.carkzis.ananke.data.database.UserEntityWithCharacters
import com.carkzis.ananke.data.database.YouDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update

class ControllableYouDao: YouDao {
    var characters = MutableStateFlow(listOf<CharacterEntity>())
    val characterGameCrossReferences = mutableListOf<CharacterGameCrossRef>()

    private val listOfUsers = dummyUserEntities
    private val userCharacterCrossReferences = mutableListOf<UserCharacterCrossRef>()

    override suspend fun insertCharacter(character: CharacterEntity) {
        characters.update { previousValues ->
            (listOf(character) + previousValues)
                .distinctBy(CharacterEntity::characterId)
                .sortedWith(idDescending())
        }
    }

    override suspend fun updateCharacter(character: CharacterEntity) {
        characters.update { previousValues ->
            previousValues.map { currentCharacter ->
                if (currentCharacter.characterId == character.characterId) character else currentCharacter
            }
        }
    }

    override fun getCharactersForUserId(userId: Long): Flow<List<CharacterEntity>> = flow {
        val characterIdsForUserId = userCharacterCrossReferences.mapNotNull {
            if (it.userId == userId) it.characterId else null
        }

        val charactersForUserId = characters.value.filter {
            characterIdsForUserId.contains(it.characterId)
        }

        emit(
            charactersForUserId
        )
    }

    override suspend fun insertOrIgnoreUserCharacterCrossRefEntities(userCharacterCrossRef: UserCharacterCrossRef) {
        if (userCharacterCrossReferences.contains(userCharacterCrossRef)) return
        userCharacterCrossReferences.add(userCharacterCrossRef)
    }

    override suspend fun insertOrIgnoreCharacterGameCrossRefEntities(characterGameCrossRef: CharacterGameCrossRef) {
        if (characterGameCrossReferences.contains(characterGameCrossRef)) return
        characterGameCrossReferences.add(characterGameCrossRef)
    }

    override fun getUserForCharacterId(characterId: Long): Flow<UserEntityWithCharacters> = flow {
        val userIdForCharacterId = userCharacterCrossReferences.map {
            it.characterId
        }.first {
            it == characterId
        }

        val userForUserId = listOfUsers.first {
            it.userId == userIdForCharacterId
        }

        val characterIdsForUserId = userCharacterCrossReferences.mapNotNull {
            if (it.userId == userIdForCharacterId) it.characterId else null
        }

        val charactersForUserId = characters.value.filter {
            characterIdsForUserId.contains(it.characterId)
        }

        emit(
            UserEntityWithCharacters(userForUserId, charactersForUserId)
        )
    }

    override fun getCharactersForGameId(gameId: Long): Flow<GameEntityWithCharacters?> = flow {
        val characterIdsForGameId = characterGameCrossReferences.filter {
            it.gameId == gameId
        }.map {
            it.characterId
        }

        val charactersForCharacterIds = characters.first().filter {
            characterIdsForGameId.contains(it.characterId)
        }

        val gameForCharacters = dummyGameEntities.firstOrNull {
            it.gameId == gameId
        }

        emit(
            gameForCharacters?.let { GameEntityWithCharacters(it, charactersForCharacterIds) }
        )
    }

    private fun idDescending() = compareBy(CharacterEntity::characterId).reversed()
}
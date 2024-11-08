package com.carkzis.ananke.testdoubles

import com.carkzis.ananke.data.database.CharacterEntity
import com.carkzis.ananke.data.database.CharacterGameCrossRef
import com.carkzis.ananke.data.database.GameEntityWithCharacters
import com.carkzis.ananke.data.database.UserCharacterCrossRef
import com.carkzis.ananke.data.database.UserEntityWithCharacters
import com.carkzis.ananke.data.database.YouDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update

class ControllableYouDao: YouDao {
    var characters = MutableStateFlow(listOf<CharacterEntity>())

    private val listOfUsers = dummyUserEntities
    private val crossReferences = mutableListOf<UserCharacterCrossRef>()

    override suspend fun insertOrUpdateCharacter(character: CharacterEntity) {
        characters.update { previousValues ->
            (listOf(character) + previousValues)
                .distinctBy(CharacterEntity::characterId)
                .sortedWith(idDescending())
        }
    }

    override fun getCharactersForUserId(userId: Long): Flow<List<CharacterEntity>> = flow {
        val characterIdsForUserId = crossReferences.mapNotNull {
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
        crossReferences.add(userCharacterCrossRef)
    }

    override suspend fun insertOrIgnoreCharacterGameCrossRefEntities(characterGameCrossRef: CharacterGameCrossRef) {
        TODO("Not yet implemented")
    }

    override fun getUserForCharacterId(characterId: Long): Flow<UserEntityWithCharacters> = flow {
        val userIdForCharacterId = crossReferences.map {
            it.characterId
        }.first {
            it == characterId
        }

        val userForUserId = listOfUsers.first {
            it.userId == userIdForCharacterId
        }

        val characterIdsForUserId = crossReferences.mapNotNull {
            if (it.userId == userIdForCharacterId) it.characterId else null
        }

        val charactersForUserId = characters.value.filter {
            characterIdsForUserId.contains(it.characterId)
        }

        emit(
            UserEntityWithCharacters(userForUserId, charactersForUserId)
        )
    }

    override fun getCharactersForGameId(gameId: Long): Flow<GameEntityWithCharacters> {
        TODO("Not yet implemented")
    }

    private fun idDescending() = compareBy(CharacterEntity::characterId).reversed()
}
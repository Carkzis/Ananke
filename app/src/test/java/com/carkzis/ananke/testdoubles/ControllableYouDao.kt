package com.carkzis.ananke.testdoubles

import com.carkzis.ananke.data.database.CharacterEntity
import com.carkzis.ananke.data.database.GameEntity
import com.carkzis.ananke.data.database.UserCharacterCrossRef
import com.carkzis.ananke.data.database.UserEntity
import com.carkzis.ananke.data.database.UserEntityWithCharacters
import com.carkzis.ananke.data.database.UserEntityWithGames
import com.carkzis.ananke.data.database.YouDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
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

    override suspend fun insertOrIgnoreCharacterUserCrossRefEntities(characterUserCrossRef: UserCharacterCrossRef) {
        crossReferences.add(characterUserCrossRef)
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

    private fun idDescending() = compareBy(CharacterEntity::characterId).reversed()
}
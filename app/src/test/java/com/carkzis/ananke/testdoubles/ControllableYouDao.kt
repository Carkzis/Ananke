package com.carkzis.ananke.testdoubles

import com.carkzis.ananke.data.database.CharacterEntity
import com.carkzis.ananke.data.database.GameEntity
import com.carkzis.ananke.data.database.UserCharacterCrossRef
import com.carkzis.ananke.data.database.UserEntity
import com.carkzis.ananke.data.database.UserEntityWithCharacters
import com.carkzis.ananke.data.database.YouDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update

class ControllableYouDao: YouDao {
    private var characters = MutableStateFlow(listOf<CharacterEntity>())

    override suspend fun insertOrUpdateCharacter(character: CharacterEntity) {
        characters.update { previousValues ->
            (listOf(character) + previousValues)
                .distinctBy(CharacterEntity::characterId)
                .sortedWith(idDescending())
        }
    }

    override fun getCharacterForId(characterId: Long): Flow<CharacterEntity?> = flow {
        emit(
            characters.value.firstOrNull {
                it.characterId == characterId
            }
        )
    }

    override suspend fun insertOrIgnoreCharacterUserCrossRefEntities(characterUserCrossRef: UserCharacterCrossRef) {
        TODO("Not yet implemented")
    }

    override fun getUserForCharacterId(characterId: Long): Flow<UserEntityWithCharacters> {
        TODO("Not yet implemented")
    }

    private fun idDescending() = compareBy(CharacterEntity::characterId).reversed()
}
package com.carkzis.ananke.testdoubles

import com.carkzis.ananke.data.model.GameCharacter
import com.carkzis.ananke.data.model.NewCharacter
import com.carkzis.ananke.data.model.User
import com.carkzis.ananke.data.repository.YouRepository
import com.carkzis.ananke.utils.RandomCharacterNameGenerator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.map

class ControllableYouRepository : YouRepository {
    var currentUserId = 1L

    private val _users = MutableSharedFlow<List<User>>(replay = 1)

    private val _characters = MutableSharedFlow<List<GameCharacter>>(replay = 1)
    private val characters get() = _characters.replayCache.firstOrNull() ?: listOf()

    private val userToCharacterMap = mutableMapOf<Long, List<GameCharacter>>()

    override fun getCharacterForUser(user: User, currentGameId: Long): Flow<GameCharacter> {
        return _characters.map { characters ->
            characters.first {
                userToCharacterMap.getOrDefault(currentGameId, listOf()).contains(it)
            }
        }
    }

    override suspend fun addNewCharacter(newCharacter: NewCharacter) {
        characters.let {
            val newGameCharacter = GameCharacter(
                id = currentUserId.toString(),
                userName = "Dave",
                character = RandomCharacterNameGenerator.generateCharacterName(),
                bio = ""
            )
            val currentCharactersForUser = userToCharacterMap[newCharacter.gameId] ?: listOf()
            val newCharactersForUser = currentCharactersForUser + newGameCharacter
            userToCharacterMap[newCharacter.gameId] = newCharactersForUser

            _characters.tryEmit((it + newGameCharacter).distinct())
        }
    }

    override suspend fun updateCharacter(character: GameCharacter, currentGameId: Long) {
        _characters.tryEmit(
            characters.map {
                if (it.id == character.id) {
                    character
                } else {
                    it
                }
            }
        )
    }

    fun emitCharacters(characters: List<GameCharacter>) {
        _characters.tryEmit(characters)
    }
}
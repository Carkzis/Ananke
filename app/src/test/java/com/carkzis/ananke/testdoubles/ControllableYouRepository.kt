package com.carkzis.ananke.testdoubles

import com.carkzis.ananke.data.database.toDomain
import com.carkzis.ananke.data.model.GameCharacter
import com.carkzis.ananke.data.model.NewCharacter
import com.carkzis.ananke.data.model.User
import com.carkzis.ananke.data.repository.YouRepository
import com.carkzis.ananke.ui.screens.you.CharacterNameTakenException
import com.carkzis.ananke.utils.RandomCharacterNameGenerator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class ControllableYouRepository : YouRepository {
    var currentUser: User? = null

    private val _characters = MutableSharedFlow<List<GameCharacter>>(replay = 1)
    private val characters get() = _characters.replayCache.firstOrNull() ?: listOf()
    private val userToCharacterMap = mutableMapOf<Long, List<GameCharacter>>()

    override fun getCharacterForUser(user: User, currentGameId: Long) = _characters.map { characters ->
        characters.firstOrNull() {
            userToCharacterMap.getOrDefault(currentGameId, listOf()).contains(it)
        } ?: GameCharacter.EMPTY
    }

    override fun getCurrentUser(): Flow<User> = flow {
        if (currentUser == null) currentUser = dummyUserEntities.first().toDomain()
        val emitableCurrentUser = currentUser
        emit(emitableCurrentUser!!)
    }

    override suspend fun addNewCharacter(newCharacter: NewCharacter) {
        characters.let {
            val newGameCharacter = GameCharacter(
                id = newCharacter.userId.toString(),
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

    override suspend fun updateCharacter(
        character: GameCharacter,
        currentGameId: Long,
        formerGameCharacter: GameCharacter
    ) {
        val characterNames = characters.map { it.character }
        if (characterNames.contains(character.character) && character.character != formerGameCharacter.character) throw CharacterNameTakenException()

        _characters.tryEmit(
            characters.map {
                if (it.id == character.id) {
                    val currentCharactersForUser = userToCharacterMap[currentGameId] ?: listOf()
                    val newCharactersForUser = (currentCharactersForUser + character).reversed().distinctBy {
                        it.id
                    }
                    userToCharacterMap[currentGameId] = newCharactersForUser
                    character
                } else {
                    it
                }
            }
        )
    }

    override suspend fun deleteCharactersForGame(gameId: Long) {
        TODO("Not yet implemented")
    }

    fun emitCharacters(characters: List<GameCharacter>) {
        _characters.tryEmit(characters)
    }
}
package com.carkzis.ananke.ui.screens.you

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carkzis.ananke.data.model.CurrentGame
import com.carkzis.ananke.data.model.GameCharacter
import com.carkzis.ananke.data.model.NewCharacter
import com.carkzis.ananke.data.network.toDomainUser
import com.carkzis.ananke.data.network.userForTesting
import com.carkzis.ananke.data.repository.YouRepository
import com.carkzis.ananke.ui.screens.game.GamingState
import com.carkzis.ananke.utils.GameStateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class YouViewModel @Inject constructor(
    gameStateUseCase: GameStateUseCase,
    private val youRepository: YouRepository
) : ViewModel() {
    val gamingState = gameStateUseCase().stateIn(
        viewModelScope,
        WhileSubscribed(5000L),
        GamingState.Loading
    )

    val currentGame = gamingState.map {
        when (gamingState.value) {
            is GamingState.Loading, GamingState.OutOfGame -> CurrentGame.EMPTY
            is GamingState.InGame -> (gamingState.value as GamingState.InGame).currentGame
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val character: StateFlow<GameCharacter> = currentGame.flatMapMerge {
        youRepository.getCharacterForUser(
            userForTesting.toDomainUser(), it.id.toLong()
        )
    }.stateIn(
        viewModelScope,
        WhileSubscribed(5000L),
        GameCharacter.EMPTY
    )

    private val _editableCharacterName = MutableStateFlow("")
    val editableCharacter = _editableCharacterName.asStateFlow()

    private val _editMode = MutableStateFlow<EditMode>(EditMode.None)
    val editMode = _editMode.asStateFlow()

    private val _message = MutableSharedFlow<String>()
    val message = _message.asSharedFlow()

    init {
        viewModelScope.launch {
            currentGame.collect {
                if (it != CurrentGame.EMPTY) {
                    val currentGameId = it.id
                    val newCharacter = NewCharacter(userId = userForTesting.id, gameId = currentGameId.toLong())
                    youRepository.addNewCharacter(newCharacter)
                }
            }
        }
    }

    fun changeCharacterName(newName: String) {
        viewModelScope.launch {
            youRepository.updateCharacter(
                character.first().copy(character = newName),
                currentGame.first().id.toLong()
            )
        }
    }

    fun changeCharacterBio(bio: String) {
        viewModelScope.launch {
            youRepository.updateCharacter(
                character.first().copy(bio = bio),
                currentGame.first().id.toLong()
            )
        }
    }

    fun beginEditingCharacterName() {
        _editMode.value = EditMode.CharacterName
        _editableCharacterName.value = character.value.character
    }

    fun editCharacterName(newName: String) {
        if (_editMode.value == EditMode.CharacterName) {
            if (newName.length < YouTextValidator.YouConstants.MINIMUM_CHARACTER_NAME_LENGTH) {
                viewModelScope.launch {
                    _message.emit(YouValidatorFailure.NAME_TOO_SHORT.message)
                }
            } else if (newName.length > YouTextValidator.YouConstants.MAXIMUM_CHARACTER_NAME_LENGTH) {
                viewModelScope.launch {
                    _message.emit(YouValidatorFailure.NAME_TOO_LONG.message)
                }
            } else {
                _editableCharacterName.value = newName
            }
        } else {
            throw CharacterNotInEditModeException()
        }
    }
}

sealed class EditMode {
    object None : EditMode()
    object CharacterName : EditMode()
}

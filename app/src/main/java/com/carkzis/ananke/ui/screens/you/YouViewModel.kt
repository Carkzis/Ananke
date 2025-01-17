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
import com.carkzis.ananke.ui.screens.you.YouConstants.Companion.MID_EDIT_MIN_LENGTH
import com.carkzis.ananke.ui.screens.you.YouTextValidator.Companion.characterBioValidator
import com.carkzis.ananke.ui.screens.you.YouTextValidator.Companion.characterNameValidator
import com.carkzis.ananke.utils.GameStateUseCase
import com.carkzis.ananke.utils.ValidatorResponse
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
    val editableCharacterName = _editableCharacterName.asStateFlow()

    private val _editableCharacterBio = MutableStateFlow("")
    val editableCharacterBio = _editableCharacterBio.asStateFlow()

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
                    try {
                        youRepository.addNewCharacter(newCharacter)
                    } catch (_: CharacterAlreadyExistsForUserException) {}
                }
            }
        }
    }

    fun changeCharacterName(newName: String) {
        val newNameValidation = characterNameValidator().validateText(newName)
        if (newNameValidation is ValidatorResponse.Fail) {
            viewModelScope.launch {
                _message.emit(newNameValidation.failureMessage)
            }
        } else {
            viewModelScope.launch {
                youRepository.updateCharacter(
                    character.first().copy(character = newName),
                    currentGame.first().id.toLong()
                )
                _editMode.value = EditMode.None
            }
        }
    }

    fun changeCharacterBio(bio: String) {
        viewModelScope.launch {
            youRepository.updateCharacter(
                character.first().copy(bio = bio),
                currentGame.first().id.toLong()
            )
            _editMode.value = EditMode.None
        }
    }

    fun beginEditingCharacterName() {
        _editMode.value = EditMode.CharacterName
        _editableCharacterName.value = character.value.character
    }

    fun beginEditingCharacterBio() {
        _editMode.value = EditMode.CharacterBio
        _editableCharacterBio.value = character.value.bio
    }

    fun cancelEdit() {
        _editMode.value = EditMode.None
    }

    fun editCharacterName(newName: String) {
        setText(
            newName,
            { _editableCharacterName.value = it },
            EditMode.CharacterName,
            characterNameValidator(minLength = MID_EDIT_MIN_LENGTH)
        )
    }

    fun editCharacterBio(newBio: String) {
        setText(
            newBio,
            { _editableCharacterBio.value = it },
            EditMode.CharacterBio,
            characterBioValidator()
        )
    }

    private fun setText(
        updatedText: String,
        onValidatedText: (String) -> Unit,
        editModeRequirement: EditMode,
        textValidator: YouTextValidator
    ) {
        try {
            if (_editMode.value != editModeRequirement) throw CharacterNotInEditModeException()
        } catch (e: CharacterNotInEditModeException) {
            viewModelScope.launch {
                _message.emit(e.message)
            }
        }

        val textValidation = textValidator.validateText(updatedText)

        if (textValidation is ValidatorResponse.Fail) {
            viewModelScope.launch {
                _message.emit(textValidation.failureMessage)
            }
        } else {
            onValidatedText(updatedText)
        }
    }
}

sealed class EditMode {
    object None : EditMode()
    object CharacterName : EditMode()
    object CharacterBio : EditMode()
}

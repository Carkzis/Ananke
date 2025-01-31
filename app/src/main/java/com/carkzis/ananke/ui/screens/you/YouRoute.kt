package com.carkzis.ananke.ui.screens.you

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.carkzis.ananke.ui.screens.game.GamingState

@Composable
fun YouRoute(
    modifier: Modifier = Modifier,
    viewModel: YouViewModel = hiltViewModel(),
    onOutOfGame: () -> Unit,
    onShowSnackbar: suspend (String) -> Boolean,
) {
    val gameState by viewModel.gamingState.collectAsStateWithLifecycle()

    val uiState by viewModel.uiState.collectAsStateWithLifecycle(YouUiState.EMPTY)

    if (gameState == GamingState.OutOfGame) {
        onOutOfGame()
    }

    YouScreen(
        modifier = modifier,
        currentGame = uiState.currentGame,
        onTitleValueChanged = { newName, isEditable ->
            if (isEditable) {
                viewModel.editCharacterName(newName)
            }
        },
        onBioValueChanged = { newBio, isEditable ->
            if (isEditable) {
                viewModel.editCharacterBio(newBio)
            }
        },
        onEnableEditCharacterName = {
            viewModel.beginEditingCharacterName()
        },
        onEnableEditCharacterBio = {
            viewModel.beginEditingCharacterBio()
        },
        onCancelEdit = {
            viewModel.cancelEdit()
        },
        characterName = if (uiState.editMode == EditMode.CharacterName) {
            uiState.editableCharacterName
        } else {
            uiState.currentCharacter.character
        },
        characterBio = if (uiState.editMode == EditMode.CharacterBio) {
            uiState.editableCharacterBio
        } else {
            uiState.currentCharacter.bio
        },
        onConfirmCharacterNameChange = {
            viewModel.changeCharacterName(uiState.editableCharacterName)
        },
        onConfirmCharacterBioChange = {
            viewModel.changeCharacterBio(uiState.editableCharacterBio)
        },
        onShowSnackbar = {
            viewModel.message.collect {
                onShowSnackbar(it)
            }
        },
        gamingState = gameState
    )
}
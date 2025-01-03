package com.carkzis.ananke.ui.screens.you

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.carkzis.ananke.data.model.CurrentGame
import com.carkzis.ananke.data.model.GameCharacter
import com.carkzis.ananke.ui.screens.game.GamingState

@Composable
fun YouRoute(
    modifier: Modifier = Modifier,
    viewModel: YouViewModel = hiltViewModel(),
    onOutOfGame: () -> Unit,
) {
    val currentGame by viewModel.currentGame.collectAsStateWithLifecycle(CurrentGame.EMPTY)
    val currentCharacter by viewModel.character.collectAsStateWithLifecycle(GameCharacter.EMPTY)
    val gameState by viewModel.gamingState.collectAsStateWithLifecycle()
    val editMode by viewModel.editMode.collectAsStateWithLifecycle()
    val editableCharacterName = viewModel.editableCharacterName.collectAsStateWithLifecycle()

    if (gameState == GamingState.OutOfGame) {
        onOutOfGame()
    }

    YouScreen(
        modifier = modifier,
        currentGame = currentGame,
        onTitleValueChanged = { newName, isEditable ->
            if (isEditable) {
                viewModel.editCharacterName(newName)
            }
        },
        onEnableEditCharacterName = {
            viewModel.beginEditingCharacterName()
        },
        characterName = if (editMode == EditMode.CharacterName) {
            editableCharacterName.value
        } else {
            currentCharacter.character
        },
        gamingState = gameState
    )
}
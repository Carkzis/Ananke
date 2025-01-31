package com.carkzis.ananke.ui.screens.you

import com.carkzis.ananke.data.model.CurrentGame
import com.carkzis.ananke.data.model.GameCharacter

data class YouUiState(
    val currentGame: CurrentGame,
    val currentCharacter: GameCharacter,
    val editableCharacterName: String,
    val editableCharacterBio: String,
    val editMode: EditMode,
) {
    companion object {
        val EMPTY = YouUiState(
            currentGame = CurrentGame.EMPTY,
            currentCharacter = GameCharacter.EMPTY,
            editableCharacterName = "",
            editableCharacterBio = "",
            editMode = EditMode.None,
        )
    }
}
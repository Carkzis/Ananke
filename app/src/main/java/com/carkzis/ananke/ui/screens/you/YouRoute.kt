package com.carkzis.ananke.ui.screens.you

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.carkzis.ananke.data.CurrentGame
import com.carkzis.ananke.ui.screens.game.GamingState

@Composable
fun YouRoute(
    modifier: Modifier = Modifier,
    viewModel: YouViewModel = hiltViewModel(),
    onOutOfGame: () -> Unit,
) {
    val currentGame by viewModel.currentGame.collectAsStateWithLifecycle(CurrentGame.EMPTY)
    val gameState by viewModel.gamingState.collectAsStateWithLifecycle()

    if (gameState == GamingState.OutOfGame) {
        onOutOfGame()
    }

    YouScreen(
        modifier = modifier,
        currentGame = currentGame,
        gamingState = gameState
    )
}
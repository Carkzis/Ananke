package com.carkzis.ananke.ui.screens.game

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun GameRoute(
    modifier: Modifier = Modifier,
    onNewGameClick: () -> Unit = {},
    onShowSnackbar: suspend (String) -> Boolean,
    viewModel: GameViewModel = hiltViewModel(),
) {
    val games by viewModel.gameList.collectAsStateWithLifecycle()
    val gameState by viewModel.gamingState.collectAsStateWithLifecycle()
    val deletableGames by viewModel.deletableGames.collectAsStateWithLifecycle()

    GameScreen(
        modifier = modifier,
        onNewGameClick = onNewGameClick,
        games = games,
        deletableGames = deletableGames,
        gamingState = gameState,
        onEnterGame = { currentGame ->
            viewModel.enterGame(currentGame)
        },
        onExitGame = {
            viewModel.exitGame()
        },
        onShowSnackbar = {
            viewModel.message.collect {
                onShowSnackbar(it)
            }
        }
    )
}
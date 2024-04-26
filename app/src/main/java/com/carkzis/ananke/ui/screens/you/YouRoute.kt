package com.carkzis.ananke.ui.screens.you

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.carkzis.ananke.data.CurrentGame
import com.carkzis.ananke.ui.screens.team.TeamViewModel

@Composable
fun YouRoute(
    modifier: Modifier = Modifier,
    viewModel: TeamViewModel = hiltViewModel(),
) {
    val currentGame = viewModel.currentGame.collectAsStateWithLifecycle(CurrentGame.EMPTY)

    YouScreen(
        modifier = modifier,
        currentGame = currentGame.value
    )
}
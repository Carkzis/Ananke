package com.carkzis.ananke.ui.screens.team

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.carkzis.ananke.data.CurrentGame

@Composable
fun TeamRoute(
    modifier: Modifier = Modifier,
    viewModel: TeamViewModel = hiltViewModel(),
) {
    val currentGame = viewModel.currentGame.collectAsStateWithLifecycle(CurrentGame.EMPTY)

    TeamScreen(
        modifier = modifier,
        currentGame = currentGame.value
    )
}
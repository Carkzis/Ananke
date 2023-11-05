package com.carkzis.ananke.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun NewGameRoute(
    modifier: Modifier = Modifier,
    viewModel: NewGameViewModel = hiltViewModel(),
    onAddGameClick: () -> Unit,
    onShowSnackbar: suspend (String) -> Boolean
) {
    val gameTitle by viewModel.gameTitle.collectAsStateWithLifecycle()
    val gameDescription by viewModel.gameDescription.collectAsStateWithLifecycle()

    NewGameScreen(
        modifier = modifier,
        viewModel = viewModel,
        gameTitle = gameTitle,
        gameDescription = gameDescription,
        onAddGameClick = onAddGameClick,
        onShowSnackbar = onShowSnackbar
    )
}
package com.carkzis.ananke.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.carkzis.ananke.data.NewGame

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun NewGameRoute(
    modifier: Modifier = Modifier,
    viewModel: NewGameViewModel = hiltViewModel(),
    onAddGameClick: () -> Unit,
    onShowSnackbar: suspend (String) -> Boolean
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    val gameTitle by viewModel.gameTitle.collectAsStateWithLifecycle()
    val gameDescription by viewModel.gameDescription.collectAsStateWithLifecycle()

    NewGameScreen(
        modifier = modifier,
        gameTitle = gameTitle,
        gameDescription = gameDescription,
        onTitleValueChanged = viewModel::updateGameTitle,
        onDescriptionValueChanged = viewModel::updateGameDescription,
        onAddGameClick = {
            viewModel.addNewGame(
                NewGame(
                    gameTitle,
                    gameDescription
                )
            )
        },
        onAddDummyGameClick = {
            viewModel.addNewGame(
                NewGame(
                    "Marc's Game",
                    "It is indescribable."
                )
            )
        },
        onAddGameSucceeds = {
            viewModel.addGameSuccessEvent.collect { wasSuccessful ->
                if (wasSuccessful) onAddGameClick()
            }
        },
        onShowSnackbar = {
            viewModel.message.collect {
                keyboardController?.hide()
                onShowSnackbar(it)
            }
        }
    )
}
package com.carkzis.ananke.ui.screens.nugame

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.carkzis.ananke.data.DEFAULT_TEAM_SIZE
import com.carkzis.ananke.data.model.NewGame
import java.util.UUID

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
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val teamSize by viewModel.teamSize.collectAsStateWithLifecycle()

    NewGameScreen(
        modifier = modifier,
        gameTitle = gameTitle,
        gameDescription = gameDescription,
        teamSize = teamSize,
        onTitleValueChanged = viewModel::updateGameTitle,
        onDescriptionValueChanged = viewModel::updateGameDescription,
        onTeamSizeChanged = viewModel::updateTeamSize,
        onAttemptAddGameClick = {
            viewModel.addNewGame(
                NewGame(
                    gameTitle,
                    gameDescription,
                    currentUser.id,
                    teamSize
                )
            )
        },
        onAddDummyGameClick = {
            val shortDummyGameUUID = UUID.randomUUID().toString().substring(0, 10)
            viewModel.addNewGame(
                NewGame(
                    "Marc's Game #$shortDummyGameUUID",
                    "It is indescribable.",
                    currentUser.id,
                    DEFAULT_TEAM_SIZE
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
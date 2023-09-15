package com.carkzis.ananke.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.carkzis.ananke.data.NewGame
import com.carkzis.ananke.navigation.GameDestination
import com.carkzis.ananke.ui.components.AnankeButton
import com.carkzis.ananke.ui.components.AnankeText

@Composable
fun NewGameScreen(
    modifier: Modifier = Modifier,
    onAddGameClick: () -> Unit,
    viewModel: NewGameViewModel = hiltViewModel()
) {
    AnankeText(
        text = "New Game",
        modifier = modifier
            .padding(8.dp)
            .testTag("${GameDestination.NEW}-title")
    )

    Column(modifier = Modifier) {

        TextField(
            value = viewModel.gameTitle,
            onValueChange = viewModel::updateGameTitle,
            modifier = modifier
                .testTag("${GameDestination.NEW}-game-title")
        )

        TextField(
            value = viewModel.gameDescription,
            onValueChange = viewModel::updateGameDescription,
            modifier = modifier
                .testTag("${GameDestination.NEW}-game-description")
        )

        AnankeButton(onClick = {
            viewModel.addNewGame(NewGame(viewModel.gameTitle, viewModel.gameDescription))
            onAddGameClick()
        }) {
            AnankeText(
                text = "Add Game",
                modifier = modifier
                    .padding(8.dp)
                    .testTag("${GameDestination.NEW}-addnewgame-button")
            )
        }

        // Test button.
        AnankeButton(onClick = {
            viewModel.addNewGame(NewGame("Marc's Game", "It is indescribable."))
            onAddGameClick()
        }) {
            AnankeText(
                text = "Add Dummy Game",
                modifier = modifier
                    .padding(8.dp)
                    .testTag("${GameDestination.NEW}-addnewgame-button-dummy")
            )
        }
    }
}
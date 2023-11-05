package com.carkzis.ananke.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.carkzis.ananke.data.NewGame
import com.carkzis.ananke.navigation.GameDestination
import com.carkzis.ananke.ui.components.AnankeButton
import com.carkzis.ananke.ui.components.AnankeMediumTitleText
import com.carkzis.ananke.ui.components.AnankeText
import com.carkzis.ananke.ui.components.AnankeTextField
import com.carkzis.ananke.ui.theme.AnankeTheme

@Composable
fun NewGameScreen(
    modifier: Modifier = Modifier,
    onAddGameClick: () -> Unit,
    viewModel: NewGameViewModel = hiltViewModel(),
    onShowSnackbar: suspend (String) -> Boolean
) {
    NewGameLaunchedEffects(viewModel, onShowSnackbar, onAddGameClick)

    Column {
        AnankeText(
            text = "New Game",
            modifier = modifier
                .padding(8.dp)
                .testTag("${GameDestination.NEW}-title"),
            textStyle = MaterialTheme.typography.headlineMedium
        )

        // TODO: also increase size of description box
        AnankeMediumTitleText(modifier = modifier, text = "Game Title")
        AnankeTextField(
            modifier = modifier.testTag("${GameDestination.NEW}-game-title"),
            value = viewModel.gameTitle,
            onValueChange = viewModel::updateGameTitle
        )

        AnankeMediumTitleText(modifier = modifier, text = "Game Description")
        AnankeTextField(
            modifier = modifier.testTag("${GameDestination.NEW}-game-description"),
            value = viewModel.gameDescription,
            onValueChange = viewModel::updateGameDescription
        )

        Spacer(modifier = Modifier.height(8.dp))

        NewGameScreenButtonRow(
            modifier = modifier,
            onAddNewGameClick = {
                viewModel.addNewGame(
                    NewGame(
                        viewModel.gameTitle,
                        viewModel.gameDescription
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
            }
        )
    }
}

@Composable
@OptIn(ExperimentalComposeUiApi::class)
private fun NewGameLaunchedEffects(
    viewModel: NewGameViewModel,
    onShowSnackbar: suspend (String) -> Boolean,
    onAddGameClick: () -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    LaunchedEffect(Unit) {
        viewModel.message.collect {
            keyboardController?.hide()
            onShowSnackbar(it)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.addGameSuccessEvent.collect { wasSuccessful ->
            if (wasSuccessful) onAddGameClick()
        }
    }
}

@Composable
private fun NewGameScreenButtonRow(
    modifier: Modifier,
    onAddNewGameClick: () -> Unit,
    onAddDummyGameClick: () -> Unit
) {
    Row(
        modifier = modifier
            .height(IntrinsicSize.Min),
        horizontalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.width(8.dp))

        AnankeButton(
            modifier = modifier
                .weight(1f)
                .fillMaxHeight(),
            onClick = {
                onAddNewGameClick()
            }) {
            AnankeText(
                text = "Add Game",
                modifier = modifier
                    .padding(8.dp)
                    .testTag("${GameDestination.NEW}-addnewgame-button")
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // TEST BUTTON
        AnankeButton(
            modifier = modifier
                .weight(1f)
                .fillMaxHeight(),
            onClick = {
                onAddDummyGameClick()
            }) {
            AnankeText(
                text = "Add Dummy Game",
                modifier = modifier
                    .padding(8.dp)
                    .testTag("${GameDestination.NEW}-addnewgame-button-dummy")
            )
        }

        Spacer(modifier = Modifier.width(8.dp))
    }
}

@Preview
@Composable
private fun NewGameTitleTextViewPreview() {
    AnankeTheme {
        AnankeTextField(
            value = "A Game Title",
            onValueChange = {},
        )
    }
}

@Preview
@Composable
private fun NewGameButtonPreview() {
    AnankeTheme {
        AnankeButton(
            onClick = {}
        ) {
            AnankeText(
                text = "Add Game",
                modifier = Modifier
                    .padding(8.dp)
            )
        }
    }
}

@Preview
@Composable
private fun NewGameButtonRowPreview() {
    AnankeTheme {
        NewGameScreenButtonRow(
            modifier = Modifier,
            onAddNewGameClick = {},
            onAddDummyGameClick = {}
        )
    }
}
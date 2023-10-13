package com.carkzis.ananke.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.carkzis.ananke.data.NewGame
import com.carkzis.ananke.navigation.GameDestination
import com.carkzis.ananke.ui.components.AnankeButton
import com.carkzis.ananke.ui.components.AnankeText
import com.carkzis.ananke.ui.theme.AnankeTheme
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun NewGameScreen(
    modifier: Modifier = Modifier,
    onAddGameClick: () -> Unit,
    viewModel: NewGameViewModel = hiltViewModel(),
    onShowSnackbar: suspend (String) -> Boolean
) {

    Column {
        AnankeText(
            text = "New Game",
            modifier = modifier
                .padding(8.dp)
                .testTag("${GameDestination.NEW}-title"),
            textStyle = MaterialTheme.typography.headlineMedium
        )

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

        Column(modifier = Modifier) {

            AnankeText(
                text = "Game Title",
                textStyle = MaterialTheme.typography.titleMedium
            )

            AnankeTextField(
                modifier = modifier,
                value = viewModel.gameTitle,
                onValueChange = viewModel::updateGameTitle,
                testTag = "${GameDestination.NEW}-game-title"
            )

            AnankeText(
                text = "Game Description",
                textStyle = MaterialTheme.typography.titleMedium
            )

            AnankeTextField(
                modifier = modifier,
                value = viewModel.gameDescription,
                onValueChange = viewModel::updateGameDescription,
                testTag = "${GameDestination.NEW}-game-description"
            )

            AnankeButton(onClick = {
                viewModel.addNewGame(NewGame(viewModel.gameTitle, viewModel.gameDescription))
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
}

@Composable
private fun AnankeTextField(modifier: Modifier = Modifier, value: String, onValueChange: (String) -> Unit, testTag: String) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        shape = RoundedCornerShape(16.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Green,
            unfocusedContainerColor = Color.Green,
            focusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .testTag(testTag)
    )
}

@Preview
@Composable
private fun NewGameTitleTextViewPreview() {
    AnankeTheme {
        AnankeTextField(
            value = "A Game Title",
            onValueChange = {},
            testTag = ""
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
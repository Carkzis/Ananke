package com.carkzis.ananke.ui.screens.nugame

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.carkzis.ananke.navigation.GameDestination
import com.carkzis.ananke.ui.components.AnankeButton
import com.carkzis.ananke.ui.components.AnankeMediumTitleText
import com.carkzis.ananke.ui.components.AnankeText
import com.carkzis.ananke.ui.components.AnankeTextField
import com.carkzis.ananke.ui.theme.AnankeTheme

@Composable
fun NewGameScreen(
    modifier: Modifier = Modifier,
    gameTitle: String,
    gameDescription: String,
    onTitleValueChanged: (String) -> Unit,
    onDescriptionValueChanged: (String) -> Unit,
    onAddGameClick: () -> Unit,
    onAddDummyGameClick: () -> Unit,
    onAddGameSucceeds: suspend () -> Unit,
    onShowSnackbar: suspend () -> Unit
) {
    NewGameLaunchedEffects(onAddGameSucceeds, onShowSnackbar)

    LazyColumn(modifier = modifier.testTag("${GameDestination.NEW}-addnewgame-lazycolumn")) {
        newGameTitle(modifier)
        gameTitleTextField(modifier, gameTitle, onTitleValueChanged)
        gameDescriptionTextField(modifier, gameDescription, onDescriptionValueChanged)
        addGameButtonRow(modifier, onAddGameClick, onAddDummyGameClick)
    }
}

private fun LazyListScope.newGameTitle(modifier: Modifier) {
    item {
        AnankeText(
            text = "New Game",
            modifier = modifier
                .padding(8.dp)
                .testTag("${GameDestination.NEW}-title"),
            textStyle = MaterialTheme.typography.headlineMedium
        )
    }
}

private fun LazyListScope.gameTitleTextField(
    modifier: Modifier,
    gameTitle: String,
    onTitleValueChanged: (String) -> Unit
) {
    item {
        AnankeMediumTitleText(modifier = modifier, text = "Game Title")
        AnankeTextField(
            modifier = modifier.testTag("${GameDestination.NEW}-game-title"),
            value = gameTitle,
            onValueChange = onTitleValueChanged
        )
    }
}

private fun LazyListScope.gameDescriptionTextField(
    modifier: Modifier,
    gameDescription: String,
    onDescriptionValueChanged: (String) -> Unit
) {
    item {
        AnankeMediumTitleText(modifier = modifier, text = "Game Description")
        AnankeTextField(
            modifier = modifier.testTag("${GameDestination.NEW}-game-description"),
            lines = 3,
            value = gameDescription,
            onValueChange = onDescriptionValueChanged
        )
    }
}

private fun LazyListScope.addGameButtonRow(
    modifier: Modifier,
    onAddGameClick: () -> Unit,
    onAddDummyGameClick: () -> Unit
) {
    item {
        Spacer(modifier = Modifier.height(8.dp))
        NewGameScreenButtonRow(
            modifier = modifier,
            onAddNewGameClick = onAddGameClick,
            onAddDummyGameClick = onAddDummyGameClick
        )
    }
}

@Composable
private fun NewGameLaunchedEffects(
    onAddGameSucceeds: suspend () -> Unit,
    onShowSnackbar: suspend () -> Unit
) {
    LaunchedEffect(Unit) {
        onAddGameSucceeds()
    }

    LaunchedEffect(Unit) {
        onShowSnackbar()
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
            onClick = onAddNewGameClick
            ) {
            AnankeText(
                text = "Add Game",
                modifier = modifier
                    .padding(8.dp)
                    .testTag("${GameDestination.NEW}-addnewgame-button")
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // TEST BUTTON.
        AnankeButton(
            modifier = modifier
                .weight(1f)
                .fillMaxHeight(),
            onClick = onAddDummyGameClick
        ) {
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

@Preview(showBackground = true)
@Composable
private fun NewGameTitleTextViewPreview() {
    AnankeTheme {
        AnankeTextField(
            value = "A Game Title",
            onValueChange = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun NewGameDescriptionTextViewPreview() {
    AnankeTheme {
        AnankeTextField(
            value = "A Game Title",
            lines = 3,
            onValueChange = {},
        )
    }
}

@Preview(showBackground = true)
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

@Preview(showBackground = true)
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

@Preview(showBackground = true)
@Composable
private fun NewGameScreenPreview() {
    AnankeTheme {
        NewGameScreen(
            gameTitle = "A Title",
            gameDescription = "A Description",
            onTitleValueChanged = {},
            onDescriptionValueChanged = {},
            onAddGameClick = {},
            onAddDummyGameClick = {},
            onAddGameSucceeds = {},
            onShowSnackbar = {}
        )
    }
}
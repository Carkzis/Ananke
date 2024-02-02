package com.carkzis.ananke.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.TransitEnterexit
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.carkzis.ananke.data.Game
import com.carkzis.ananke.navigation.GameDestination
import com.carkzis.ananke.ui.components.AnankeButton
import com.carkzis.ananke.ui.components.AnankeText
import com.carkzis.ananke.ui.theme.AnankeTheme
import com.carkzis.ananke.ui.theme.Typography

@Composable
fun GameScreen(
    modifier: Modifier = Modifier,
    onNewGameClick: () -> Unit = {},
    viewModel: GameScreenViewModel = hiltViewModel()
) {
    val games = viewModel.gameList.collectAsStateWithLifecycle()
    val lazyListState = rememberLazyListState()

    LazyColumn(modifier = modifier.testTag("${GameDestination.HOME}-gameslist"), lazyListState) {
        item {
            AnankeText(
                text = "Games",
                modifier = modifier
                    .padding(8.dp)
                    .testTag("${GameDestination.HOME}-title"),
                textStyle = MaterialTheme.typography.headlineMedium
            )
        }

        games.value.forEach { game ->
            item(key = game.id) {
                GameCard(modifier.testTag("${GameDestination.HOME}-gameitem"), game)
            }
        }

        item {
            AnankeButton(onClick = onNewGameClick) {
                AnankeText(
                    text = "Add New Game",
                    modifier = modifier
                        .padding(8.dp)
                        .testTag("${GameDestination.HOME}-to-${GameDestination.NEW}-button")
                )
            }
        }
    }
}

@Composable
private fun GameCard(
    modifier: Modifier,
    game: Game
) {
    val enterGameDialog = remember { mutableStateOf(false) }
    val onEnterGameClick = {
        enterGameDialog.value = true
    }

    if (enterGameDialog.value) {
        GameEnterDialog(
            modifier,
            onDismissRequest = {
                enterGameDialog.value = false
            },
            game
        )
    }

    Card(
        modifier = modifier
            .padding(4.dp)
            .testTag("${GameDestination.HOME}-gameCard"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Box(modifier = modifier
            .padding(16.dp)
        ) {
            Column {
                GameCardTitle(game, modifier)

                Row {
                    GameCardDescriptionText(game, modifier)
                    GameCardEnterButton(modifier, onEnterGameClick)
                }

                GameCardMetadata(modifier)
            }
        }
    }
}

@Composable
private fun GameEnterDialog(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    game: Game
) {
    AlertDialog(
        modifier = modifier,
        onDismissRequest = onDismissRequest,
        title = { Text(game.name) },
        text = { Text("Enter game?") },
        confirmButton = {
            Icon(
                imageVector = Icons.Rounded.Done,
                contentDescription = null,
                modifier = Modifier.padding(16.dp)
            )
        },
        dismissButton = {
            Icon(
                imageVector = Icons.Rounded.Close,
                contentDescription = null,
                modifier = Modifier.padding(16.dp)
            )
        },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    )
}

@Composable
private fun GameCardTitle(game: Game, modifier: Modifier) {
    AnankeText(text = game.name, textStyle = Typography.titleMedium)
    GameCardDivider(modifier)
}

@Composable
private fun GameCardDivider(modifier: Modifier) {
    Spacer(modifier = modifier.height(8.dp))
    Divider(color = MaterialTheme.colorScheme.onSurface, thickness = 1.dp)
    Spacer(modifier = modifier.height(8.dp))
}

@Composable
private fun RowScope.GameCardDescriptionText(game: Game, modifier: Modifier) {
    AnankeText(
        text = game.description, textAlign = TextAlign.Start, modifier = modifier
            .height(96.dp)
            .weight(1f)
    )
}

@Composable
private fun RowScope.GameCardEnterButton(modifier: Modifier, onEnterGameClick: () -> Unit) {
    IconButton(
        onClick = onEnterGameClick,
        modifier = modifier.align(CenterVertically)
    ) {
        Icon(
            imageVector = Icons.Filled.TransitEnterexit,
            contentDescription = null,
        )
    }
}

@Composable
private fun ColumnScope.GameCardMetadata(modifier: Modifier) {
    GameCardDivider(modifier)
    AnankeText(
        text = "Players: 0",
        textAlign = TextAlign.Start,
        modifier = modifier.align(Alignment.Start),
        textStyle = Typography.bodySmall
    )
}

@Preview
@Composable
private fun GameCardPreview() {
    AnankeTheme {
        GameCard(
            modifier = Modifier,
            game = Game(
                id = "",
                name = "The Game",
                description = "This is a game.",
            )
        )
    }
}

@Preview
@Composable
private fun GameEnterDialogPreview() {
    AnankeTheme {
        GameEnterDialog(
            onDismissRequest = {},
            game = Game(
                id = "",
                name = "The Game",
                description = "This is a game.",
            )
        )
    }
}
package com.carkzis.ananke.ui.screens.game

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
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
import androidx.compose.runtime.LaunchedEffect
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
import com.carkzis.ananke.data.model.CurrentGame
import com.carkzis.ananke.data.model.Game
import com.carkzis.ananke.data.model.toCurrentGame
import com.carkzis.ananke.navigation.GameDestination
import com.carkzis.ananke.ui.components.AnankeButton
import com.carkzis.ananke.ui.components.AnankeText
import com.carkzis.ananke.ui.theme.AnankeTheme
import com.carkzis.ananke.ui.theme.Typography

@Composable
fun GameScreen(
    modifier: Modifier = Modifier,
    onNewGameClick: () -> Unit = {},
    onEnterGame: (CurrentGame) -> Unit = {},
    onExitGame: () -> Unit = {},
    games: List<Game>,
    gamingState: GamingState,
    onShowSnackbar: suspend () -> Unit = {}
) {
    GameScreenLaunchedEffects(onShowSnackbar)
    val lazyListState = rememberLazyListState()
    when (gamingState) {
        is GamingState.Loading -> {}
        is GamingState.OutOfGame -> {
            OutOfGameScreen(modifier, lazyListState, games, onEnterGame, onNewGameClick)
        }
        is GamingState.InGame -> {
            InGameScreen(modifier, gamingState, onExitGame)
        }
    }
}

@Composable
private fun GameScreenLaunchedEffects(
    onShowSnackbar: suspend () -> Unit
) {
    LaunchedEffect(Unit) {
        onShowSnackbar()
    }
}

@Composable
private fun OutOfGameScreen(
    modifier: Modifier,
    lazyListState: LazyListState,
    games: List<Game>,
    onEnterGame: (CurrentGame) -> Unit,
    onNewGameClick: () -> Unit
) {
    LazyColumn(modifier = modifier.testTag("${GameDestination.HOME}-gameslist"), lazyListState) {
        gameScreenTitle(modifier)
        listOfAvailableGames(games, modifier, onEnterGame)
        newGameButton(onNewGameClick, modifier)
    }
}

@Composable
private fun InGameScreen(
    modifier: Modifier,
    gamingState: GamingState.InGame,
    onExitGame: () -> Unit
) {
    LazyColumn(modifier = modifier.testTag("${GameDestination.HOME}-current-game-column")) {
        currentGameTitle(gamingState, modifier)
        currentGameDescription(gamingState, modifier)
        exitGameButton(onExitGame, modifier)
    }
}

private fun LazyListScope.gameScreenTitle(modifier: Modifier) {
    item {
        AnankeText(
            text = "Games",
            modifier = modifier
                .padding(8.dp)
                .testTag("${GameDestination.HOME}-title"),
            textStyle = MaterialTheme.typography.headlineMedium
        )
    }
}

private fun LazyListScope.listOfAvailableGames(
    games: List<Game>,
    modifier: Modifier,
    onEnterGame: (CurrentGame) -> Unit
) {
    games.forEach { game ->
        item(key = game.id) {
            GameCard(
                modifier = modifier,
                onEnterGame = onEnterGame,
                game = game
            )
        }
    }
}

private fun LazyListScope.currentGameTitle(
    gamingState: GamingState.InGame,
    modifier: Modifier
) {
    item {
        AnankeText(
            text = gamingState.currentGame.name,
            modifier = modifier
                .padding(8.dp)
                .testTag("${GameDestination.HOME}-current-game-title"),
            textStyle = MaterialTheme.typography.headlineMedium
        )
    }
}

private fun LazyListScope.currentGameDescription(
    gamingState: GamingState.InGame,
    modifier: Modifier
) {
    item {
        AnankeText(
            text = gamingState.currentGame.description,

            modifier = modifier
                .padding(8.dp),
        )
    }
    item { Spacer(modifier = modifier.height(8.dp)) }
}

private fun LazyListScope.exitGameButton(
    onExitGame: () -> Unit,
    modifier: Modifier
) {
    item { GameScreenExitGameButton(onExitGame, modifier) }
}

private fun LazyListScope.newGameButton(
    onNewGameClick: () -> Unit,
    modifier: Modifier
) {
    item { GameScreenNewGameButton(onNewGameClick, modifier) }
}

@Composable
private fun GameCard(
    modifier: Modifier,
    onEnterGame: (CurrentGame) -> Unit,
    game: Game
) {
    val enterGameDialog = remember { mutableStateOf(false) }
    val onEnterGameClick = { enterGameDialog.value = true }

    if (enterGameDialog.value) {
        GameEnterDialog(
            modifier,
            onDismissRequest = {
                enterGameDialog.value = false
            },
            onConfirmRequest = {
                enterGameDialog.value = false
                onEnterGame(game.toCurrentGame())
            },
            game
        )
    }

    Card(
        modifier = modifier
            .padding(4.dp)
            .testTag("${GameDestination.HOME}-gamecard"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        GameCardBox(modifier, game, onEnterGameClick)
    }
}

@Composable
private fun GameEnterDialog(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    onConfirmRequest: () -> Unit,
    game: Game
) {
    AlertDialog(
        modifier = modifier.testTag("${GameDestination.HOME}-enter-alert"),
        onDismissRequest = onDismissRequest,
        title = { Text(game.name) },
        text = { Text("Enter game?") },
        confirmButton = { GameEnterConfirmIcon(onConfirmRequest) },
        dismissButton = { GameEnterDismissIcon(onDismissRequest) },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    )
}

@Composable
private fun GameCardBox(
    modifier: Modifier,
    game: Game,
    onEnterGameClick: () -> Unit
) {
    Box(
        modifier = modifier
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
        modifier = modifier
            .align(CenterVertically)
            .testTag("${GameDestination.HOME}-game-enter-button")
    ) {
        Icon(
            imageVector = Icons.Filled.TransitEnterexit,
            contentDescription = null,
        )
    }
}

@Composable
private fun GameEnterConfirmIcon(onConfirmRequest: () -> Unit) {
    Icon(
        imageVector = Icons.Rounded.Done,
        contentDescription = null,
        modifier = Modifier
            .padding(16.dp)
            .clickable { onConfirmRequest() }
            .testTag("${GameDestination.HOME}-enter-alert-confirm")
    )
}

@Composable
private fun GameEnterDismissIcon(onDismissRequest: () -> Unit) {
    Icon(
        imageVector = Icons.Rounded.Close,
        contentDescription = null,
        modifier = Modifier
            .padding(16.dp)
            .clickable { onDismissRequest() }
            .testTag("${GameDestination.HOME}-enter-alert-reject")
    )
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

@Composable
private fun GameScreenNewGameButton(onNewGameClick: () -> Unit, modifier: Modifier) {
    AnankeButton(onClick = onNewGameClick) {
        AnankeText(
            text = "Add New Game",
            modifier = modifier
                .padding(8.dp)
                .testTag("${GameDestination.HOME}-to-${GameDestination.NEW}-button")
        )
    }
}

@Composable
private fun GameScreenExitGameButton(onExitGame: () -> Unit, modifier: Modifier) {
    AnankeButton(
        modifier = modifier.testTag("${GameDestination.HOME}-exit-current-game"),
        onClick = onExitGame
    ) {
        AnankeText(
            text = "Exit Game",
            modifier = modifier
                .padding(8.dp)
        )
    }
}

@Preview
@Composable
private fun GameCardPreview() {
    AnankeTheme {
        GameCard(
            modifier = Modifier,
            onEnterGame = {},
            game = Game(
                id = "",
                name = "The Game",
                description = "This is a game.",
                creatorId = "",
            )
        )
    }
}

@Preview
@Composable
private fun GameScreenNewGameButtonPreview() {
    AnankeTheme {
        GameScreenNewGameButton(
            onNewGameClick = {},
            modifier = Modifier
        )
    }
}

@Preview
@Composable
private fun GameScreenExitGameButtonPreview() {
    AnankeTheme {
        GameScreenExitGameButton(
            onExitGame = {},
            modifier = Modifier
        )
    }
}

@Preview
@Composable
private fun GameEnterDialogPreview() {
    AnankeTheme {
        GameEnterDialog(
            onDismissRequest = {},
            onConfirmRequest = {},
            game = Game(
                id = "",
                name = "The Game",
                description = "This is a game.",
                creatorId = "",
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun OutOfGameScreenPreview() {
    AnankeTheme {
        GameScreen(
            games = listOf(
                Game(
                    id = "1",
                    name = "Game 1",
                    description = "This is a game.",
                    creatorId = "",
                ),
                Game(
                    id = "2",
                    name = "Game 2",
                    description = "This is another game.",
                    creatorId = "",
                )
            ),
            gamingState = GamingState.OutOfGame
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun InGameScreenPreview() {
    AnankeTheme {
        GameScreen(
            games = listOf(),
            gamingState = GamingState.InGame(
                CurrentGame(
                    id = "3",
                    name = "Game 3",
                    description = "We are in the game."
                )
            )
        )
    }
}
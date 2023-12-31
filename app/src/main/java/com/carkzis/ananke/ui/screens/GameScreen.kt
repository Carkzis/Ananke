package com.carkzis.ananke.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.carkzis.ananke.data.Game
import com.carkzis.ananke.navigation.GameDestination
import com.carkzis.ananke.ui.components.AnankeButton
import com.carkzis.ananke.ui.components.AnankeText

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
                GameItem(modifier.testTag("${GameDestination.HOME}-gameitem"), game)
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
private fun GameItem(
    modifier: Modifier,
    game: Game
) {
    Column(modifier = modifier.padding(8.dp)) {
        AnankeText(text = "Game: ${game.name}")
        AnankeText(text = "Description: ${game.description}")
    }
}
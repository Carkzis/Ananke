package com.carkzis.ananke.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.TransitEnterexit
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GameItem(
    modifier: Modifier,
    game: Game
) {
    Card(
        modifier = modifier.padding(4.dp),
        shape = RoundedCornerShape(16.dp),
        onClick = {}
    ) {
        Box(modifier = modifier.padding(16.dp)) {
            Column {
                AnankeText(text = game.name, textStyle = Typography.titleMedium)
                Spacer(modifier = modifier.height(8.dp))
                Divider(color = MaterialTheme.colorScheme.onSurface, thickness = 1.dp)
                Spacer(modifier = modifier.height(8.dp))

                Row {
                    Column(modifier = Modifier.weight(1f)) {
                        AnankeText(text = game.description, textAlign = TextAlign.Start, modifier = modifier.align(Alignment.Start).height(96.dp))
                        Spacer(modifier = modifier.height(8.dp))
                        AnankeText(text = "Players: 0", textAlign = TextAlign.Start, modifier = modifier.align(Alignment.Start), textStyle = Typography.bodySmall)
                    }

                    IconButton(onClick = {}) {
                        Icon(
                            imageVector = Icons.Filled.TransitEnterexit,
                            contentDescription = null
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun GameItemPreview() {
    AnankeTheme {
        GameItem(
            modifier = Modifier,
            game = Game(
                id = "",
                name = "The Game",
                description = "This is a game. This is a game. This is a game. This is a game. This is a game.",
            )
        )
    }
}
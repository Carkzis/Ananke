package com.example.ananke.ui.screens

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ananke.ui.components.AnankeText

@Composable
fun GameScreen(modifier: Modifier = Modifier, viewModel: GameScreenViewModel = hiltViewModel()) {
    val games = viewModel.gameList.collectAsStateWithLifecycle()
    val lazyListState = rememberLazyListState()

    LazyColumn(modifier = modifier, lazyListState) {
        item { AnankeText(text = "Games", modifier = modifier.padding(8.dp)) }

        games.value.forEach { game ->
            item(key = game.id) {
                Row {
                    AnankeText(text = "Game: ${game.name}, Description: ${game.description}")
                }
            }
        }
    }
}
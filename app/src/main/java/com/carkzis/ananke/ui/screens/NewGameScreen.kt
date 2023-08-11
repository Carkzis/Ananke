package com.carkzis.ananke.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.carkzis.ananke.navigation.AnankeDestination
import com.carkzis.ananke.navigation.GameDestination
import com.carkzis.ananke.ui.components.AnankeButton
import com.carkzis.ananke.ui.components.AnankeText

@Composable
fun NewGameScreen(
    modifier: Modifier = Modifier,
    onAddGameClick: () -> Unit,
    viewModel: NewGameScreenViewModel = hiltViewModel()
) {
    AnankeText(
        text = "New Game",
        modifier = modifier
            .padding(8.dp)
            .testTag("${GameDestination.NEW}-title")
    )

    Column(modifier = Modifier) {
        AnankeButton(onClick = onAddGameClick) {
            AnankeText(
                text = "Add Game",
                modifier = modifier.padding(8.dp).testTag("${GameDestination.HOME}-to-${GameDestination.NEW}-button")
            )
        }
    }
}
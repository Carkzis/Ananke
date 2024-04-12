package com.carkzis.ananke.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.carkzis.ananke.data.CurrentGame
import com.carkzis.ananke.navigation.AnankeDestination
import com.carkzis.ananke.navigation.GameDestination
import com.carkzis.ananke.ui.components.AnankeText

@Composable
fun TeamScreen(currentGame: CurrentGame, modifier: Modifier = Modifier) {
    AnankeText(text = "Team", modifier = modifier
        .padding(8.dp)
        .testTag("${AnankeDestination.TEAM}-title"))

    AnankeText(
        text = currentGame.name,
        modifier = modifier
            .padding(8.dp)
            .testTag("${AnankeDestination.TEAM}-current-game"),
        textStyle = MaterialTheme.typography.headlineMedium
    )
}
package com.carkzis.ananke.ui.screens.team

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.carkzis.ananke.data.CurrentGame
import com.carkzis.ananke.navigation.AnankeDestination
import com.carkzis.ananke.ui.components.AnankeText
import com.carkzis.ananke.ui.theme.AnankeTheme

@Composable
fun TeamScreen(currentGame: CurrentGame, modifier: Modifier = Modifier) {
    Column {
        AnankeText(
            text = "Team",
            modifier = modifier
                .padding(8.dp)
                .testTag("${AnankeDestination.TEAM}-title"),
            textStyle = MaterialTheme.typography.headlineMedium
        )

        AnankeText(
            text = currentGame.name,
            modifier = modifier
                .padding(8.dp)
                .testTag("${AnankeDestination.TEAM}-current-game"),
            textStyle = MaterialTheme.typography.headlineSmall
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TeamScreenPreview() {
    AnankeTheme {
        TeamScreen(
            currentGame = CurrentGame(
                id = "1",
                name = "Preview Game",
                description = "This is not a real game."
            )
        )
    }
}
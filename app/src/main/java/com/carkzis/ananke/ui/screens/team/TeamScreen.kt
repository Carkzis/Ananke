package com.carkzis.ananke.ui.screens.team

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.carkzis.ananke.data.CurrentGame
import com.carkzis.ananke.data.User
import com.carkzis.ananke.data.network.NetworkUser
import com.carkzis.ananke.navigation.AnankeDestination
import com.carkzis.ananke.ui.components.AnankeText
import com.carkzis.ananke.ui.screens.game.GamingState
import com.carkzis.ananke.ui.theme.AnankeTheme

@Composable
fun TeamScreen(
    currentGame: CurrentGame,
    gamingState: GamingState,
    modifier: Modifier = Modifier,
    users: List<User> = listOf()
) {
    when (gamingState) {
        is GamingState.Loading -> {}
        is GamingState.OutOfGame -> {}
        is GamingState.InGame -> {
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

                AnankeText(
                    text = "Team Members",
                    modifier = modifier
                        .padding(8.dp)
                        .testTag("${AnankeDestination.TEAM}-team-member-title"),
                    textStyle = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Left
                )

                AnankeText(
                    text = "There are currently no users in the game.",
                    modifier = modifier
                        .padding(8.dp)
                        .testTag("${AnankeDestination.TEAM}-team-member-title"),
                    textStyle = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Left
                )

                AnankeText(
                    text = "Available Users",
                    modifier = modifier
                        .padding(8.dp)
                        .testTag("${AnankeDestination.TEAM}-users-title"),
                    textStyle = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Left
                )

                LazyColumn(modifier = modifier.testTag("${AnankeDestination.TEAM}-team-members-column")) {
                    users.forEach { user ->
                        item(key = user.id) {
                            AnankeText(
                                text = user.name,
                                modifier = modifier
                                    .padding(8.dp)
                                    .testTag("${AnankeDestination.TEAM}-users-title"),
                                textStyle = MaterialTheme.typography.bodySmall,
                                textAlign = TextAlign.Left
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TeamScreenPreview() {
    AnankeTheme {
        val currentGame = CurrentGame(
            id = "1",
            name = "Preview Game",
            description = "This is not a real game."
        )
        TeamScreen(
            currentGame = currentGame,
            gamingState = GamingState.InGame(currentGame),
            users = listOf(
                User(id = 1, name = "Zidun"),
                User(id = 2, name = "Vivu"),
                User(id = 3, name = "Steinur")
            )
        )
    }
}
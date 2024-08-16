package com.carkzis.ananke.ui.screens.team

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.TagFaces
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.carkzis.ananke.data.CurrentGame
import com.carkzis.ananke.data.User
import com.carkzis.ananke.navigation.AnankeDestination
import com.carkzis.ananke.ui.components.AnankeText
import com.carkzis.ananke.ui.screens.game.GamingState
import com.carkzis.ananke.ui.theme.AnankeTheme

@Composable
fun TeamScreen(
    currentGame: CurrentGame,
    gamingState: GamingState,
    modifier: Modifier = Modifier,
    users: List<User> = listOf(),
    teamMembers: List<User> = listOf(),
    onAddUser: (User) -> Unit = {}
) {
    when (gamingState) {
        is GamingState.Loading -> {}
        is GamingState.OutOfGame -> {}
        is GamingState.InGame -> {
            val lazyListState = rememberLazyListState()
            LazyColumn(
                modifier = modifier.testTag("${AnankeDestination.TEAM}-team-column"),
                lazyListState
            ) {
                item {
                    AnankeText(
                        text = "Team",
                        modifier = modifier
                            .padding(8.dp)
                            .testTag("${AnankeDestination.TEAM}-title"),
                        textStyle = MaterialTheme.typography.headlineMedium
                    )
                }

                item {
                    AnankeText(
                        text = currentGame.name,
                        modifier = modifier
                            .padding(8.dp)
                            .testTag("${AnankeDestination.TEAM}-current-game"),
                        textStyle = MaterialTheme.typography.headlineSmall
                    )
                }

                item {
                    AnankeText(
                        text = "Team Members",
                        modifier = modifier
                            .padding(8.dp)
                            .testTag("${AnankeDestination.TEAM}-team-member-title"),
                        textStyle = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Left
                    )
                }

                if (teamMembers.isEmpty()) {
                    item {
                        AnankeText(
                            text = "There are currently no users in the game.",
                            modifier = modifier
                                .padding(8.dp)
                                .testTag("${AnankeDestination.TEAM}-no-team-members-text"),
                            textStyle = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Left
                        )
                    }
                } else {
                    teamMembers.forEach { teamMember ->
                        item(key = "${teamMember.id}-tm") {
                            AnankeText(
                                text = teamMember.name,
                                modifier = modifier
                                    .padding(8.dp)
                                    .testTag("${AnankeDestination.TEAM}-team-member-name"),
                                textStyle = MaterialTheme.typography.bodySmall,
                                textAlign = TextAlign.Left
                            )
                        }
                    }
                }

                item {
                    AnankeText(
                        text = "Available Users",
                        modifier = modifier
                            .padding(8.dp)
                            .testTag("${AnankeDestination.TEAM}-users-title"),
                        textStyle = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Left
                    )
                }

                users.forEach { user ->
                    item(key = "${user.id}-pt") {
                        UserCard(
                            modifier = modifier,
                            onAddUser = onAddUser,
                            user = user
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun UserCard(
    modifier: Modifier,
    onAddUser: (User) -> Unit,
    user: User
) {
    Card(
        modifier = modifier
            .padding(4.dp)
            .testTag("${AnankeDestination.TEAM}-usercard"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        UserCardBox(
            modifier = modifier,
            onAddUser = onAddUser,
            user = user
        )
    }
}

@Composable
private fun UserCardBox(
    modifier: Modifier,
    onAddUser: (User) -> Unit,
    user: User
) {
    Box(
        modifier = modifier
            .padding(16.dp)
    ) {
        Row {
            Icon(
                modifier = modifier
                    .align(CenterVertically),
                imageVector = Icons.Filled.TagFaces,
                contentDescription = null,
            )
            AnankeText(
                text = user.name,
                modifier = modifier
                    .align(CenterVertically)
                    .weight(1f)
                    .testTag("${AnankeDestination.TEAM}-users-title"),
                textStyle = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
            IconButton(
                onClick = {
                    onAddUser(user)
                },
                modifier = modifier
                    .align(CenterVertically)
                    .testTag("${AnankeDestination.TEAM}-add-user-button")
            ) {
                Icon(
                    imageVector = Icons.Filled.GroupAdd,
                    contentDescription = null,
                )
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
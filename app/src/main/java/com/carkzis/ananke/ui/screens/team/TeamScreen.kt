package com.carkzis.ananke.ui.screens.team

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.TagFaces
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.carkzis.ananke.data.model.CurrentGame
import com.carkzis.ananke.data.model.GameCharacter
import com.carkzis.ananke.data.model.User
import com.carkzis.ananke.navigation.AnankeDestination
import com.carkzis.ananke.ui.components.AnankeButton
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
    deletableTeamMembers: List<User> = listOf(),
    event: TeamEvent = TeamEvent.CloseDialogue,
    onAddUser: (User) -> Unit = {},
    onViewTeamMember: (User) -> Unit = {},
    onViewUser: (User) -> Unit = {},
    onRemoveTeamMember: (User) -> Unit = {},
    onViewTeamMemberForRemoval: (User)  -> Unit = {},
    onDismissDialogue: () -> Unit = {},
    onShowSnackbar: suspend () -> Unit = {}
) {
    LaunchedEffect(Unit) {
        onShowSnackbar()
    }

    when (gamingState) {
        is GamingState.Loading -> {}
        is GamingState.OutOfGame -> {}
        is GamingState.InGame -> {
            InGameTeamScreen(
                modifier = modifier,
                currentGame = currentGame,
                teamMembers = teamMembers,
                users = users,
                deletableTeamMembers = deletableTeamMembers,
                onAddUser = onAddUser,
                onViewTeamMember = onViewTeamMember,
                onViewUser = onViewUser,
                onRemoveTeamMember =onRemoveTeamMember,
                onViewTeamMemberForRemoval = onViewTeamMemberForRemoval,
                onDismissDialogue = onDismissDialogue,
                event = event
            )
        }
    }
}

@Composable
private fun InGameTeamScreen(
    modifier: Modifier,
    currentGame: CurrentGame,
    teamMembers: List<User>,
    deletableTeamMembers: List<User>,
    users: List<User>,
    onAddUser: (User) -> Unit,
    onViewTeamMember: (User) -> Unit,
    onViewUser: (User) -> Unit,
    onDismissDialogue: () -> Unit,
    onRemoveTeamMember: (User) -> Unit = {},
    onViewTeamMemberForRemoval: (User)  -> Unit = {},
    event: TeamEvent = TeamEvent.CloseDialogue
) {
    if (event is TeamEvent.TeamMemberDialogueShow) {
        TeamMemberDialogue(onDismissDialogue, modifier, event)
    }

    if (event is TeamEvent.UserDialogueShow) {
        UserDialogue(onDismissDialogue, modifier, event)
    }

    if (event is TeamEvent.DeleteTeamMemberConfirmationDialogueShow) {
        DeleteTeamMemberConfirmationDialogue(
            onDismissDialogue = onDismissDialogue,
            modifier = modifier,
            event = event,
            onRemoveTeamMember = {
                onRemoveTeamMember(event.teamMember)
            }
        )
    }

    val lazyListState = rememberLazyListState()
    LazyColumn(
        modifier = modifier.testTag("${AnankeDestination.TEAM}-team-column"),
        lazyListState
    ) {
        teamScreenTitle(modifier)
        currentGameTitle(currentGame, modifier)
        teamMembers(
            modifier,
            teamMembers,
            deletableTeamMembers,
            onViewTeamMember,
            onViewTeamMemberForRemoval,

        )
        availableUsers(modifier, users, onAddUser, onViewUser)
    }
}

@Composable
private fun TeamMemberDialogue(
    onDismissDialogue: () -> Unit,
    modifier: Modifier,
    event: TeamEvent.TeamMemberDialogueShow
) {
    Dialog(
        onDismissRequest = onDismissDialogue,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        Card(
            modifier = modifier
                .padding(16.dp)
                .fillMaxSize()
                .testTag("${AnankeDestination.TEAM}-team-member-dialogue"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
        ) {
            AnankeText(
                text = "Team Member",
                modifier = modifier
                    .padding(top = 32.dp)
                    .testTag("${AnankeDestination.TEAM}-team-member-dialogue-title"),
                textStyle = MaterialTheme.typography.headlineLarge
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                AnankeText(
                    text = "Name",
                    modifier = modifier
                        .padding(8.dp),
                    textStyle = MaterialTheme.typography.titleLarge
                )
                AnankeText(
                    text = event.teamMember.name,
                    modifier = modifier
                        .padding(8.dp),
                    textStyle = MaterialTheme.typography.bodyLarge
                )
                AnankeText(
                    text = "Character",
                    modifier = modifier
                        .padding(8.dp),
                    textStyle = MaterialTheme.typography.titleLarge
                )
                AnankeText(
                    text = event.character.character,
                    modifier = modifier
                        .padding(8.dp),
                    textStyle = MaterialTheme.typography.bodyLarge
                )
                AnankeText(
                    text = "Bio",
                    modifier = modifier
                        .padding(8.dp),
                    textStyle = MaterialTheme.typography.titleLarge
                )
                AnankeText(
                    text = event.character.bio.ifEmpty { "No bio available." },
                    modifier = modifier
                        .padding(8.dp),
                    textStyle = MaterialTheme.typography.bodyLarge
                )

                AnankeButton(onClick = onDismissDialogue) {
                    AnankeText(
                        text = "Close",
                        modifier = modifier
                            .padding(8.dp)
                            .testTag("${AnankeDestination.TEAM}-team-member-dialogue-close-button"),
                        textStyle = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}

@Composable
private fun UserDialogue(
    onDismissDialogue: () -> Unit,
    modifier: Modifier,
    event: TeamEvent.UserDialogueShow
) {
    Dialog(
        onDismissRequest = onDismissDialogue,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        Card(
            modifier = modifier
                .padding(16.dp)
                .fillMaxSize()
                .testTag("${AnankeDestination.TEAM}-user-dialogue"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
        ) {
            AnankeText(
                text = "User",
                modifier = modifier
                    .padding(top = 32.dp)
                    .testTag("${AnankeDestination.TEAM}-user-dialogue-title"),
                textStyle = MaterialTheme.typography.headlineLarge
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                AnankeText(
                    text = "Name",
                    modifier = modifier
                        .padding(8.dp),
                    textStyle = MaterialTheme.typography.titleLarge
                )
                AnankeText(
                    text = event.user.name,
                    modifier = modifier
                        .padding(8.dp),
                    textStyle = MaterialTheme.typography.bodyLarge
                )
                AnankeButton(onClick = onDismissDialogue) {
                    AnankeText(
                        text = "Close",
                        modifier = modifier
                            .padding(8.dp)
                            .testTag("${AnankeDestination.TEAM}-user-dialogue-close-button"),
                        textStyle = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}

@Composable
fun DeleteTeamMemberConfirmationDialogue(
    onDismissDialogue: () -> Unit,
    modifier: Modifier,
    event: TeamEvent.DeleteTeamMemberConfirmationDialogueShow,
    onRemoveTeamMember: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismissDialogue,
    ) {
        Card(
            modifier = modifier
                .padding(16.dp)
                .testTag("${AnankeDestination.TEAM}-delete-team-member-confirmation-dialogue"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
        ) {
            Column(
                modifier = modifier
                    .padding(16.dp)
                    .testTag("${AnankeDestination.TEAM}-delete-team-member-confirmation-dialogue-column"),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Icon(
                    modifier = modifier
                        .padding(8.dp)
                        .testTag("${AnankeDestination.TEAM}-delete-team-member-confirmation-dialogue-icon"),
                    imageVector = Icons.Filled.Delete,
                    contentDescription = null,
                )
                AnankeText(
                    text = "Are you sure you want to remove ${event.teamMember.name} from the team?",
                    modifier = modifier
                        .padding(8.dp)
                        .testTag("${AnankeDestination.TEAM}-delete-team-member-confirmation-dialogue-text"),
                    textStyle = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = modifier.fillMaxWidth()
                ) {
                    AnankeButton(
                        onClick = {
                            onRemoveTeamMember()
                            onDismissDialogue()
                        },
                        modifier = modifier.testTag("${AnankeDestination.TEAM}-confirm-remove-team-member-button")
                    ) {
                        AnankeText(
                            text = "Yes",
                            modifier = modifier.padding(8.dp)
                                .width(40.dp),
                            textStyle = MaterialTheme.typography.bodyLarge
                        )
                    }
                    AnankeButton(
                        onClick = onDismissDialogue,
                        modifier = modifier.testTag("${AnankeDestination.TEAM}-cancel-remove-team-member-button")
                    ) {
                        AnankeText(
                            text = "No",
                            modifier = modifier.padding(8.dp)
                                .width(40.dp),
                            textStyle = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
    }
}

private fun LazyListScope.teamScreenTitle(modifier: Modifier) {
    item {
        AnankeText(
            text = "Team",
            modifier = modifier
                .padding(8.dp)
                .testTag("${AnankeDestination.TEAM}-title"),
            textStyle = MaterialTheme.typography.headlineMedium
        )
    }
}

private fun LazyListScope.currentGameTitle(
    currentGame: CurrentGame,
    modifier: Modifier
) {
    item {
        AnankeText(
            text = currentGame.name,
            modifier = modifier
                .padding(8.dp)
                .testTag("${AnankeDestination.TEAM}-current-game"),
            textStyle = MaterialTheme.typography.headlineSmall
        )
    }
}

private fun LazyListScope.teamMembers(
    modifier: Modifier,
    teamMembers: List<User>,
    deletableTeamMembers: List<User>,
    onViewTeamMember: (User) -> Unit,
    onViewTeamMemberForRemoval: (User)  -> Unit = {},
) {
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
                text = "There are currently no users in the game. You will need to delete the game from the home menu.",
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
                TeamMemberCard(
                    modifier = modifier,
                    user = teamMember,
                    onViewTeamMember = onViewTeamMember,
                    removable = deletableTeamMembers.contains(teamMember),
                    onViewTeamMemberForRemoval = onViewTeamMemberForRemoval
                )
            }
        }
    }
}

private fun LazyListScope.availableUsers(
    modifier: Modifier,
    users: List<User>,
    onAddUser: (User) -> Unit,
    onViewUser: (User) -> Unit,
) {
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
                onViewUser = onViewUser,
                user = user
            )
        }
    }
}

@Composable
private fun TeamMemberCard(
    modifier: Modifier,
    user: User,
    removable: Boolean,
    onViewTeamMember: (User) -> Unit,
    onViewTeamMemberForRemoval: (User)  -> Unit = {},
) {
    Card(
        modifier = modifier
            .padding(4.dp)
            .testTag("${AnankeDestination.TEAM}-tm-card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        TeamMemberCardBox(
            modifier = modifier,
            user = user,
            removable = removable,
            onViewTeamMember = onViewTeamMember,
            onViewTeamMemberForRemoval = onViewTeamMemberForRemoval
        )
    }
}

@Composable
private fun TeamMemberCardBox(
    modifier: Modifier,
    user: User,
    removable: Boolean,
    onViewTeamMember: (User) -> Unit,
    onViewTeamMemberForRemoval: (User)  -> Unit = {},
) {
    Box(
        modifier = modifier
            .padding(16.dp)
            .testTag("${AnankeDestination.TEAM}-tm-card-box")
            .clickable {
                onViewTeamMember(user)
            }
    ) {
        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row {
                Icon(
                    modifier = modifier
                        .align(CenterVertically),
                    imageVector = Icons.Filled.Shield,
                    contentDescription = null,
                )
                AnankeText(
                    text = user.name,
                    modifier = modifier
                        .align(CenterVertically)
                        .width(intrinsicSize = IntrinsicSize.Max)
                        .padding(start = 16.dp)
                        .testTag("${AnankeDestination.TEAM}-team-member-name"),
                    textStyle = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Left,
                )
            }

            if (removable) {
                Icon(
                    modifier = modifier
                        .align(CenterVertically)
                        .padding(start = 16.dp)
                        .testTag("${AnankeDestination.TEAM}-remove-team-member-icon")
                        .clickable {
                            onViewTeamMemberForRemoval(user)
                        },
                    imageVector = Icons.Filled.Delete,
                    contentDescription = null,
                )
            }
        }
    }
}

@Composable
private fun UserCard(
    modifier: Modifier,
    onAddUser: (User) -> Unit,
    onViewUser: (User) -> Unit,
    user: User,
) {
    Card(
        modifier = modifier
            .padding(4.dp)
            .testTag("${AnankeDestination.TEAM}-user-card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        UserCardBox(
            modifier = modifier,
            onAddUser = onAddUser,
            onViewUser = onViewUser,
            user = user
        )
    }
}

@Composable
private fun UserCardBox(
    modifier: Modifier,
    onAddUser: (User) -> Unit,
    onViewUser: (User) -> Unit,
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
                    .testTag("${AnankeDestination.TEAM}-user-name")
                    .clickable {
                        onViewUser(user)
                    },
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
private fun TeamScreenNoTeamMembersPreview() {
    AnankeTheme {
        val currentGame = CurrentGame(
            id = "1",
            name = "Preview Game Without Team",
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

@Preview(showBackground = true)
@Composable
private fun TeamScreenWithTeamMembersPreview() {
    AnankeTheme {
        val currentGame = CurrentGame(
            id = "1",
            name = "Preview Game With Team",
            description = "This is not a real game."
        )
        TeamScreen(
            currentGame = currentGame,
            gamingState = GamingState.InGame(currentGame),
            users = listOf(
                User(id = 3, name = "Steinur")
            ),
            teamMembers = listOf(
                User(id = 1, name = "Zidun"),
                User(id = 2, name = "Vivu")
            ),
            deletableTeamMembers = listOf(
                User(id = 2, name = "Vivu")
            ),
        )
    }
}

@Preview
@Composable
private fun TeamMemberDialoguePreview() {
    AnankeTheme {
        TeamMemberDialogue(
            onDismissDialogue = {},
            modifier = Modifier,
            event = TeamEvent.TeamMemberDialogueShow(
                teamMember = User(id = 1, name = "Zidun"),
                character = GameCharacter(
                    id = "1",
                    userName = "Marc",
                    character = "Zidun",
                    bio = "This is a test character.",
                )
            )
        )
    }
}

@Preview
@Composable
private fun UserDialoguePreview() {
    AnankeTheme {
        UserDialogue(
            onDismissDialogue = {},
            modifier = Modifier,
            event = TeamEvent.UserDialogueShow(
                user = User(id = 1, name = "Zidun"),
            )
        )
    }
}

@Preview
@Composable
private fun DeleteTeamMemberConfirmationDialoguePreview(
) {
    AnankeTheme {
        DeleteTeamMemberConfirmationDialogue(
            onDismissDialogue = {},
            modifier = Modifier,
            event = TeamEvent.DeleteTeamMemberConfirmationDialogueShow(
                teamMember = User(id = 1, name = "Zidun"),
            ),
            onRemoveTeamMember = {}
        )
    }
}
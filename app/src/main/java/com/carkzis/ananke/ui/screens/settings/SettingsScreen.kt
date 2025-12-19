package com.carkzis.ananke.ui.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.carkzis.ananke.data.model.User
import com.carkzis.ananke.navigation.AnankeDestination
import com.carkzis.ananke.navigation.GameDestination
import com.carkzis.ananke.ui.components.AnankeButton
import com.carkzis.ananke.ui.components.AnankeText
import com.carkzis.ananke.ui.theme.AnankeTheme

@Composable
fun SettingsScreen(
    currentUser: User,
    onConfirmUsername: (String) -> Unit = {}
) {
    Column {
        AnankeText(
            text = "Settings",
            modifier = Modifier
                .padding(8.dp)
                .testTag("${AnankeDestination.SETTINGS}-title"),
            textStyle = MaterialTheme.typography.headlineMedium
        )
        AnankeText(
            text = currentUser.name,
            modifier = Modifier
                .padding(8.dp)
                .testTag("${AnankeDestination.SETTINGS}-current-user"),
            textStyle = MaterialTheme.typography.headlineSmall
        )

        SettingsList(
            currentUser = currentUser,
            onConfirmUsername = onConfirmUsername
        )
    }
}

@Composable
private fun SettingsList(
    currentUser: User,
    onConfirmUsername: (String) -> Unit = {}
) {
    Column(
        verticalArrangement = Arrangement.Center
    ) {
        ChangeUsernameOption(
            currentUser = currentUser,
            onConfirmUsername = onConfirmUsername
        )
    }
}

@Composable
private fun ChangeUsernameOption(
    currentUser: User,
    onConfirmUsername: (String) -> Unit = {}
) {
    var changeUserDialogue by remember { mutableStateOf(false) }

    if (changeUserDialogue) {
        ChangeUsernameDialogue(
            currentUser = currentUser,
            onConfirmUsername = { newName ->
                changeUserDialogue = false
                onConfirmUsername(newName)
            },
            onDismiss = {
                changeUserDialogue = false
            }
        )
    }

    AnankeButton(
        onClick = {
            changeUserDialogue = true
        }
    ) {
        AnankeText(
            text = "Change username",
        )
    }
}

@Composable
fun ChangeUsernameDialogue(
    modifier: Modifier = Modifier,
    currentUser: User,
    onConfirmUsername: (String) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
    ) {
        Card(
            modifier = modifier
                .testTag("${GameDestination.HOME}-change-username-dialog"),
        ) {
            var newName by remember { mutableStateOf(currentUser.name) }

            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AnankeText(
                    text = "Enter new username:"
                )

                TextField(
                    value = newName,
                    onValueChange = {
                        newName = it
                    },
                )

                Row {
                    AnankeButton(
                        modifier = Modifier.weight(1f)
                            .testTag("${GameDestination.HOME}-change-username-confirm-button"),
                        onClick = {
                            onConfirmUsername(newName)
                        }
                    ) {
                        AnankeText(
                            text = "Confirm"
                        )
                    }

                    AnankeButton(
                        modifier = Modifier.weight(1f)
                            .testTag("${GameDestination.HOME}-change-username-dismiss-button"),
                        onClick = onDismiss,
                    ) {
                        AnankeText(
                            text = "Cancel"
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    AnankeTheme {
        SettingsScreen(
            currentUser = User(
                id = 1L,
                name = "Sample User"
            )
        )
    }
}

@Preview
@Composable
fun ChangeUsernameDialoguePreview() {
    AnankeTheme {
        ChangeUsernameDialogue(
            currentUser = User(
                id = 1L,
                name = "Sample User"
            ),
            onConfirmUsername = {},
            onDismiss = {}
        )
    }
}

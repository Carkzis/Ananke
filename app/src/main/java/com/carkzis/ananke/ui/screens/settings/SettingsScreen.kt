package com.carkzis.ananke.ui.screens.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.carkzis.ananke.data.model.User
import com.carkzis.ananke.navigation.AnankeDestination
import com.carkzis.ananke.ui.components.AnankeText
import com.carkzis.ananke.ui.theme.AnankeTheme

@Composable
fun SettingsScreen(
    currentUser: User
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

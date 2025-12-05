package com.carkzis.ananke.ui.screens.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import com.carkzis.ananke.data.model.User
import com.carkzis.ananke.ui.components.AnankeText

@Composable
fun SettingsScreen(
    currentUser: User
) {
    Column {
        AnankeText(text = "Settings Screen")
        AnankeText(text = "Welcome, ${currentUser.name}!")
    }
}

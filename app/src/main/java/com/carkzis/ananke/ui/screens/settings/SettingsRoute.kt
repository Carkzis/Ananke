package com.carkzis.ananke.ui.screens.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.carkzis.ananke.data.model.User

@Composable
fun SettingsRoute(
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()

    SettingsScreen(
        currentUser = currentUser ?: User.EMPTY
    )
}
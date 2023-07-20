package com.example.ananke.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.ananke.ui.components.AnankeBottomBar
import com.example.ananke.navigation.AnankeNavHost
import com.example.ananke.ui.components.AnankeBackground
import com.example.ananke.ui.components.AnankeTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnankeApp(
    appState: AnankeAppState = rememberAnankeAppState()
) {
    AnankeBackground {
        Scaffold(
            modifier = Modifier,
            topBar = {
                     AnankeTopBar()
            },
            bottomBar = {
                AnankeBottomBar(modifier = Modifier,
                    destinations = appState.destinations,
                    currentDestination = appState.currentDestination,
                    onNavigate = appState::navigateToDestination
                )
            }
        ) { padding ->
            Column(modifier = Modifier.padding(padding)) {
                AnankeNavHost(appState = appState)
            }
        }
    }
}

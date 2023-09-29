package com.carkzis.ananke.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.carkzis.ananke.ui.components.AnankeBottomBar
import com.carkzis.ananke.navigation.AnankeNavHost
import com.carkzis.ananke.ui.components.AnankeBackground
import com.carkzis.ananke.ui.components.AnankeTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnankeApp(
    appState: AnankeAppState = rememberAnankeAppState()
) {
    AnankeBackground {
        val snackbarHostState = remember { SnackbarHostState() }
        Scaffold(
            modifier = Modifier,
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
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
                AnankeNavHost(appState = appState, onShowSnackbar = { message ->
                    snackbarHostState.showSnackbar(
                        message = message, duration = SnackbarDuration.Short
                    ) == SnackbarResult.Dismissed
                })
            }
        }
    }
}

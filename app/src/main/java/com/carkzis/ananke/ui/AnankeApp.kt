package com.carkzis.ananke.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.compose.currentBackStackEntryAsState
import com.carkzis.ananke.data.repository.GameRepository
import com.carkzis.ananke.navigation.AnankeDestination
import com.carkzis.ananke.navigation.AnankeNavHost
import com.carkzis.ananke.navigation.GameDestination
import com.carkzis.ananke.ui.components.AnankeBackground
import com.carkzis.ananke.ui.components.AnankeBottomBar
import com.carkzis.ananke.ui.components.AnankeTopBar
import com.carkzis.ananke.utils.GameStateUseCase

@Composable
fun AnankeApp(
    gameRepository: GameRepository,
    appState: AnankeAppState = rememberAnankeAppState(
        gameState = GameStateUseCase(gameRepository)
    ),
) {
    AnankeBackground {
        val snackbarHostState = remember { SnackbarHostState() }
        val searchBarLaunched by appState.launchSearchBar.collectAsStateWithLifecycle()
        val currentDestination by appState.navController.currentBackStackEntryAsState()
        var inGame by remember { mutableStateOf(false) }

        Scaffold(
            modifier = Modifier,
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            topBar = {
                AnankeTopBar(
                    searchEnabled = searchIsEnabledForDestination(currentDestination, inGame),
                    onSearchClicked = {
                        appState.openSearchBar(
                            currentDestination?.destination?.route
                                ?: "${AnankeDestination.GAME}/${GameDestination.HOME}"
                        )
                    }
                )
            },
            bottomBar = {
                AnankeBottomBar(modifier = Modifier,
                    destinations = appState.destinations,
                    availabilities = appState.availabilityMap.collectAsState(initial = mapOf()).value,
                    currentDestination = appState.currentDestination,
                    onNavigate = appState::navigateToDestination
                )
            }
        ) { padding ->
            if (searchBarLaunched.isOpen) {
                AlertDialog(
                    modifier = Modifier
                        .testTag( "global-search-dialogue"),
                    onDismissRequest = {
                        appState.closeSearchBar(currentDestination?.id ?: "${AnankeDestination.GAME}/${GameDestination.HOME}")
                    },
                    title = { Text("Perform a search") },
                    text = {
                        TextField(
                            value = "",
                            onValueChange = { /* Handle search query change */ },
                            label = { Text("Perform a search") }
                        )
                    },
                    confirmButton = {
                        Icon(
                            imageVector = Icons.Rounded.Done,
                            contentDescription = null,
                            modifier = Modifier
                                .padding(16.dp)
                                .clickable {
                                    appState.closeSearchBar(
                                        currentDestination?.id
                                            ?: "${AnankeDestination.GAME}/${GameDestination.HOME}"
                                    )
                                }
                                .testTag("global-search-bar-confirm-button")
                        )
                    },
                    dismissButton = {
                        Icon(
                            imageVector = Icons.Rounded.Close,
                            contentDescription = null,
                            modifier = Modifier
                                .padding(16.dp)
                                .clickable {
                                    appState.closeSearchBar(
                                        currentDestination?.id
                                            ?: "${AnankeDestination.GAME}/${GameDestination.HOME}"
                                    )
                                }
                                .testTag("global-search-bar-close-button")
                        )
                    },
                )
            }

            Column(modifier = Modifier.padding(padding)) {
                AnankeNavHost(
                    appState = appState,
                    onShowSnackbar = { message ->
                        snackbarHostState.showSnackbar(
                            message = message, duration = SnackbarDuration.Short
                        ) == SnackbarResult.Dismissed
                    },
                    onInGame = {
                        inGame = it
                    }
                )
            }
        }
    }
}

@Composable
private fun searchIsEnabledForDestination(
    currentDestination: NavBackStackEntry?,
    inGame: Boolean,
): Boolean =
    when (currentDestination?.destination?.route) {
        "${AnankeDestination.GAME}/${GameDestination.HOME}" -> {
            !inGame
        }
        "${AnankeDestination.GAME}/${GameDestination.NEW}" -> false
        AnankeDestination.TEAM.toString() -> true
        AnankeDestination.YOU.toString() -> false
        else -> false
    }

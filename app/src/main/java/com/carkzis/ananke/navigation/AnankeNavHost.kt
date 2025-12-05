package com.carkzis.ananke.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.carkzis.ananke.ui.AnankeAppState
import com.carkzis.ananke.ui.screens.game.GameRoute
import com.carkzis.ananke.ui.screens.team.TeamRoute
import com.carkzis.ananke.ui.screens.nugame.NewGameRoute
import com.carkzis.ananke.ui.screens.you.YouRoute

@Composable
fun AnankeNavHost(
    modifier: Modifier = Modifier,
    appState: AnankeAppState,
    onShowSnackbar: suspend (String) -> Boolean,
    searchText: String,
    startDestination: String = AnankeDestination.GAME.toString()
) {
    val navController = appState.navController
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        navigation(
            route = startDestination,
            startDestination = "${AnankeDestination.GAME}/${GameDestination.HOME}"
        ) {
            composable(route = "${AnankeDestination.GAME}/${GameDestination.HOME}") {
                GameRoute(
                    onNewGameClick = {
                        appState.updateSearchText("")
                        navController.navigate("${AnankeDestination.GAME}/${GameDestination.NEW}") {
                            launchSingleTop = true
                        }
                    },
                    onShowSnackbar = onShowSnackbar,
                    searchText = searchText,
                )
            }
            composable(
                route = "${AnankeDestination.GAME}/${GameDestination.NEW}"
            ) {
                NewGameRoute(
                    onAddGameClick = {
                        navController.popBackStack()
                    },
                    onShowSnackbar = onShowSnackbar
                )
            }
        }
        composable(route = AnankeDestination.TEAM.toString()) {
            TeamRoute(
                onOutOfGame = {
                    navController.navigateUp()
                },
                onShowSnackbar = onShowSnackbar,
                searchText = searchText,
            )
        }
        composable(route = AnankeDestination.YOU.toString()) {
            YouRoute(
                onOutOfGame = {
                    navController.navigateUp()
                },
                onShowSnackbar = onShowSnackbar
            )
        }
    }
}
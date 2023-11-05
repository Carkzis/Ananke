package com.carkzis.ananke.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.carkzis.ananke.ui.AnankeAppState
import com.carkzis.ananke.ui.screens.GameScreen
import com.carkzis.ananke.ui.screens.NewGameRoute
import com.carkzis.ananke.ui.screens.NewGameScreen
import com.carkzis.ananke.ui.screens.TeamScreen
import com.carkzis.ananke.ui.screens.YouScreen

@Composable
fun AnankeNavHost(
    modifier: Modifier = Modifier,
    appState: AnankeAppState,
    onShowSnackbar: suspend (String) -> Boolean,
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
                GameScreen(onNewGameClick = {
                    navController.navigate("${AnankeDestination.GAME}/${GameDestination.NEW}") {
                        launchSingleTop = true
                    }
                })
            }
            composable(
                route = "${AnankeDestination.GAME}/${GameDestination.NEW}"
            ) {
                NewGameRoute(onAddGameClick = {
                    navController.popBackStack()
                }, onShowSnackbar = onShowSnackbar)
            }
        }
        composable(route = AnankeDestination.TEAM.toString()) {
            TeamScreen()
        }
        composable(route = AnankeDestination.YOU.toString()) {
            YouScreen()
        }
    }
}
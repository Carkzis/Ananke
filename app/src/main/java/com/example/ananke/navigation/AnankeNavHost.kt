package com.example.ananke.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.example.ananke.ui.AnankeAppState
import com.example.ananke.ui.screens.GameScreen
import com.example.ananke.ui.screens.NewGameScreen
import com.example.ananke.ui.screens.TeamScreen
import com.example.ananke.ui.screens.YouScreen

@Composable
fun AnankeNavHost(
    modifier: Modifier = Modifier,
    appState: AnankeAppState,
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
                NewGameScreen()
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
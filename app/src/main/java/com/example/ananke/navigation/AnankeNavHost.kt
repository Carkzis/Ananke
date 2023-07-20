package com.example.ananke.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.ananke.ui.AnankeAppState
import com.example.ananke.ui.screens.GameScreen
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
        composable(route = startDestination) {
            GameScreen()
        }
        composable(route = AnankeDestination.TEAM.toString()) {
            TeamScreen()
        }
        composable(route = AnankeDestination.YOU.toString()) {
            YouScreen()
        }
    }
}
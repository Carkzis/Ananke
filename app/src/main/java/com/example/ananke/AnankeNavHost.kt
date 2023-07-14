package com.example.ananke

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

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
            AnankeText(text = "Screen 1")
        }
        composable(route = AnankeDestination.TEAM.toString()) {
            AnankeText(text = "Screen 2")
        }
        composable(route = AnankeDestination.YOU.toString()) {
            AnankeText(text = "Screen 3")
        }
    }
}
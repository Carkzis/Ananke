package com.example.ananke

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun AnankeNavHost(
    modifier: Modifier = Modifier,
    appState: AnankeAppState,
    startDestination: String = AnankeDestination.SCREEN_ONE.toString()
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
        composable(route = AnankeDestination.SCREEN_TWO.toString()) {
            AnankeText(text = "Screen 2")
        }
        composable(route = AnankeDestination.SCREEN_THREE.toString()) {
            AnankeText(text = "Screen 3")
        }
    }
}
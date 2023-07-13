package com.example.ananke

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun AnankeNavHost(
    modifier: Modifier = Modifier,
    appState: AnankeAppState,
    startDestination: String = AnankeDestinations.CREATE.toString()
) {
    val navController = appState.navController
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(startDestination) {
            CreateScreen()
        }
    }
}
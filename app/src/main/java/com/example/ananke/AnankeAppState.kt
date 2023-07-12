package com.example.ananke

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

class AnankeAppState(
    val navController: NavHostController
) {
    val currentDestination: NavDestination?
        @Composable get() = navController
            .currentBackStackEntryAsState().value?.destination

    val destinations = AnankeDestinations.values().toList()
}

@Composable
fun rememberAnankeAppState(
    navController: NavHostController = rememberNavController()
) : AnankeAppState {
    return remember(navController) {
        AnankeAppState(navController)
    }
}
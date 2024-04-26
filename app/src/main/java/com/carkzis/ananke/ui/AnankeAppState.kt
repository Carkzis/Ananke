package com.carkzis.ananke.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.carkzis.ananke.navigation.AnankeDestination

class AnankeAppState(
    val navController: NavHostController
) {
    val currentDestination: NavDestination?
        @Composable get() = navController
            .currentBackStackEntryAsState().value?.destination

    val destinations = AnankeDestination.values().toList()

    fun navigateToDestination(destination: AnankeDestination) {
        navController.navigate(destination.toString(), navigationOptions())
    }

    private fun navigationOptions(): NavOptionsBuilder.() -> Unit = {
        popUpTo(navController.graph.findStartDestination().id) {
            saveState = true
        }
        restoreState = true
    }

}

@Composable
fun rememberAnankeAppState(
    navController: NavHostController = rememberNavController()
) : AnankeAppState {
    return remember(navController) {
        AnankeAppState(navController)
    }
}
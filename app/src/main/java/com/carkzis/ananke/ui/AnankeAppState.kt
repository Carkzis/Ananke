package com.carkzis.ananke.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.carkzis.ananke.data.GameRepository
import com.carkzis.ananke.navigation.AnankeDestination
import com.carkzis.ananke.ui.screens.game.GamingState
import com.carkzis.ananke.utils.GameStateUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class AnankeAppState(
    val navController: NavHostController,
    coroutineScope: CoroutineScope,
    gameStateUseCase: GameStateUseCase
) {
    val currentDestination: NavDestination?
        @Composable get() = navController
            .currentBackStackEntryAsState().value?.destination

    val destinations = AnankeDestination.values().toList()

    val availabilityMap: Flow<Map<AnankeDestination, Boolean>> =
        gameStateUseCase().stateIn(
            coroutineScope,
            SharingStarted.WhileSubscribed(5000L),
            GamingState.OutOfGame
        ).map { gamingState ->
            val screenIsAvailable = gamingState is GamingState.InGame
            destinations.associateWith { destination ->
                when (destination) {
                    AnankeDestination.GAME -> true
                    else -> screenIsAvailable
                }
            }
        }

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
    navController: NavHostController = rememberNavController(),
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    gameState: GameStateUseCase
) : AnankeAppState {
    return remember(navController) {
        AnankeAppState(navController, coroutineScope, gameState)
    }
}
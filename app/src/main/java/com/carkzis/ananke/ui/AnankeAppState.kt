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
import com.carkzis.ananke.navigation.AnankeDestination
import com.carkzis.ananke.navigation.GameDestination
import com.carkzis.ananke.ui.screens.game.GamingState
import com.carkzis.ananke.utils.GameStateUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filter
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

    private val _launchSearchBar = MutableStateFlow(
        SearchDialogueState(
            isOpen = false,
            currentDestination = "${AnankeDestination.GAME}/${GameDestination.HOME}"
        )
    )
    val launchSearchBar: StateFlow<SearchDialogueState> = _launchSearchBar

    private val _searchText: MutableStateFlow<String> = MutableStateFlow("")
    val searchText: StateFlow<String> = _searchText

    val gameState = gameStateUseCase().stateIn(
        coroutineScope,
        SharingStarted.WhileSubscribed(5000L),
        GamingState.OutOfGame
    )

    val availabilityMap: Flow<Map<AnankeDestination, Boolean>> =
        gameStateUseCase().stateIn(
            coroutineScope,
            SharingStarted.WhileSubscribed(5000L),
            GamingState.OutOfGame
        ).map { gamingState ->
            val screenIsAvailable = gamingState is GamingState.InGame
            destinations.associateWith { destination ->
                when (destination) {
                    AnankeDestination.GAME, AnankeDestination.SETTINGS -> true
                    else -> screenIsAvailable
                }
            }
        }

    fun navigateToDestination(destination: AnankeDestination) {
        _searchText.value = ""
        navController.navigate(destination.toString(), navigationOptions())
    }

    fun openSearchBar(destination: String) {
        _launchSearchBar.value = SearchDialogueState(
            isOpen = true,
            currentDestination = destination
        )
    }

    fun closeSearchBar(destination: String) {
        _launchSearchBar.value = SearchDialogueState(
            isOpen = false,
            currentDestination = destination
        )
    }

    fun updateSearchText(newText: String) {
        _searchText.value = newText
    }

    private fun navigationOptions(): NavOptionsBuilder.() -> Unit = {
        popUpTo(navController.graph.findStartDestination().id) {
            saveState = true
        }
        restoreState = true
    }

}

data class SearchDialogueState(
    val isOpen: Boolean,
    val currentDestination: String
)

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
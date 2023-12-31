package com.carkzis.ananke

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.composable
import androidx.navigation.createGraph
import androidx.navigation.testing.TestNavHostController
import com.carkzis.ananke.navigation.AnankeDestination
import com.carkzis.ananke.ui.AnankeAppState
import com.carkzis.ananke.ui.rememberAnankeAppState
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.shadows.ShadowLog

@RunWith(RobolectricTestRunner::class)
class AnankeAppStateTest {

    @get:Rule val composeTestRule = createComposeRule()

    private lateinit var appState: AnankeAppState

    @Before
    fun setUp() {
        ShadowLog.stream = System.out
    }

    @Test
    fun `anankeAppState has the expected destinations`() {
        val expectedDestinations = AnankeDestination.values()

        composeTestRule.setContent {
            appState = rememberAnankeAppState()
        }

        assertEquals(3, appState.destinations.size)
        assertTrue(appState.destinations.contains(expectedDestinations[0]))
        assertTrue(appState.destinations.contains(expectedDestinations[1]))
        assertTrue(appState.destinations.contains(expectedDestinations[2]))
    }

    @Test
    fun `anankeAppState has the expected default initial destination`() {
        val expectedInitialDestination = AnankeDestination.GAME.toString()

        var actualInitialDestination: String? =  null
        composeTestRule.setContent {
            val navController = rememberTestNavController()
            appState = rememberAnankeAppState(navController = navController)
            actualInitialDestination = appState.currentDestination?.route
        }

        assertEquals(expectedInitialDestination, actualInitialDestination)
    }

    @Test
    fun `anankeAppState has the expected new destination`() {
        val expectedInitialDestination = AnankeDestination.TEAM.toString()

        var actualInitialDestination: String? =  null
        composeTestRule.setContent {
            val navController = rememberTestNavController()
            appState = rememberAnankeAppState(navController = navController)
            actualInitialDestination = appState.currentDestination?.route

            LaunchedEffect(Unit) {
                navController.setCurrentDestination(expectedInitialDestination)
            }
        }

        assertEquals(expectedInitialDestination, actualInitialDestination)
    }
}

@Composable
fun rememberTestNavController() : TestNavHostController {
    val context = LocalContext.current
    return remember {
        TestNavHostController(context = context).apply {
            navigatorProvider.addNavigator(ComposeNavigator())
            graph = createGraph(startDestination = AnankeDestination.GAME.toString()) {
                AnankeDestination.values().forEach { destination ->
                    composable(destination.toString()) {}
                }
            }
        }
    }
}
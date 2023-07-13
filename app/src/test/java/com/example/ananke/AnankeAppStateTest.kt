package com.example.ananke

import androidx.compose.ui.test.junit4.createComposeRule
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
        val expectedDestinations = listOf(
            AnankeDestination.SCREEN_ONE,
            AnankeDestination.SCREEN_TWO,
            AnankeDestination.SCREEN_THREE
        )

        composeTestRule.setContent {
            appState = rememberAnankeAppState()
        }

        assertEquals(3, appState.destinations.size)
        assertTrue(appState.destinations.contains(expectedDestinations[0]))
        assertTrue(appState.destinations.contains(expectedDestinations[1]))
        assertTrue(appState.destinations.contains(expectedDestinations[2]))
    }
}
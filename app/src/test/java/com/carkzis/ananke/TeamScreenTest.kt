package com.carkzis.ananke

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.carkzis.ananke.data.CurrentGame
import com.carkzis.ananke.navigation.AnankeDestination
import com.carkzis.ananke.testdoubles.ControllableGameRepository
import com.carkzis.ananke.ui.screens.TeamScreen
import com.carkzis.ananke.ui.screens.TeamViewModel
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@HiltAndroidTest
@RunWith(RobolectricTestRunner::class)
@Config(application = HiltTestApplication::class)
class TeamScreenTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun `display current game name`() {
        val gameRepository = ControllableGameRepository()
        val viewModel = TeamViewModel(GameStateUseCase(gameRepository))

        val currentGame = CurrentGame("1", "A Game", "A Description")
        gameRepository.emitCurrentGame(currentGame)

        composeTestRule.apply {
            composeTestRule.setContent {
                val actualCurrentGame = viewModel
                    .currentGame
                    .collectAsStateWithLifecycle(initialValue = CurrentGame.EMPTY)
                    .value
                TeamScreen(
                    currentGame = actualCurrentGame,
                )
            }

            onNodeWithTag("${AnankeDestination.TEAM}-current-game")
                .assertTextContains(currentGame.name)
        }
    }
}
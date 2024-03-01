package com.carkzis.ananke

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertAll
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onChildren
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import com.carkzis.ananke.data.Game
import com.carkzis.ananke.navigation.GameDestination
import com.carkzis.ananke.ui.screens.GameScreen
import com.carkzis.ananke.ui.screens.GamingState
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
class GameScreenTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun `no title when loading`() {
        composeTestRule.apply {
            composeTestRule.setContent {
                GameScreen(
                    games = dummyGames(),
                    gamingState = GamingState.Loading
                )
            }

            onNodeWithTag("${GameDestination.HOME}-title")
                .assertDoesNotExist()
        }
    }

    @Test
    fun `expected list of games displays with expected details when outside game`() {
        composeTestRule.apply {
            composeTestRule.setContent {
                GameScreen(
                    games = dummyGames(),
                    gamingState = GamingState.OutOfGame
                )
            }

            onNodeWithTag("${GameDestination.HOME}-title")
                .assertExists()
            onNodeWithTag("${GameDestination.HOME}-gameslist")
                .assertExists()
            onAllNodesWithTag("${GameDestination.HOME}-gamecard").apply {
                fetchSemanticsNodes().forEachIndexed { index, _ ->
                    val currentCard = get(index)
                    currentCard.apply {
                        assertTextContains(dummyGames()[index].name)
                        assertTextContains(dummyGames()[index].description)
                        onNodeWithTag("${GameDestination.HOME}-game-enter-button")
                            .assertHasClickAction()
                    }
                }
            }
        }
    }

    @Test
    fun `enter expected game via dialog`() {

    }

    @Test
    fun `exit a game so that a list of games displays again`() {

    }

    @Test
    fun `dismiss an enter game dialog to remove it`() {

    }

    fun dummyGames() = listOf(
        Game("abc", "My First Game", "It is the first one."),
        Game("def", "My Second Game", "It is the second one."),
        Game("ghi", "My Third Game", "It is the third one.")
    )

}
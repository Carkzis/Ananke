package com.carkzis.ananke

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertAll
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.filter
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasAnyChild
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onChildren
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
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
                        hasAnyChild(hasText(dummyGames()[index].name))
                        hasAnyChild(hasText(dummyGames()[index].description))
                        hasAnyChild(hasTestTag("${GameDestination.HOME}-game-enter-button"))
                    }
                }
            }
        }
    }

    @Test
    fun `enter expected game via dialog`() {
        composeTestRule.apply {
            composeTestRule.setContent {
                GameScreen(
                    games = dummyGames(),
                    gamingState = GamingState.OutOfGame
                )
            }

            onAllNodesWithTag("${GameDestination.HOME}-gamecard")
                .filter(
                    hasAnyChild(
                        hasText(dummyGames().first().name, substring = true)
                    )
                )
                .assertCountEquals(1)
                .onFirst()
                .performClick()

            // TODO: Check dialog opens.
            // TODO: Check enter expected game.
        }

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
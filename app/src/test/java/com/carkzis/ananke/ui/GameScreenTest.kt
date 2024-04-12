package com.carkzis.ananke.ui

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.SemanticsNodeInteractionCollection
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.filter
import androidx.compose.ui.test.hasAnyChild
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onChildren
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onLast
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.carkzis.ananke.data.CurrentGame
import com.carkzis.ananke.data.Game
import com.carkzis.ananke.data.toCurrentGame
import com.carkzis.ananke.navigation.GameDestination
import com.carkzis.ananke.testdoubles.ControllableGameRepository
import com.carkzis.ananke.ui.screens.game.GameScreen
import com.carkzis.ananke.ui.screens.game.GameViewModel
import com.carkzis.ananke.ui.screens.game.GamingState
import com.carkzis.ananke.utils.GameStateUseCase
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import org.junit.Assert.assertEquals
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
            initialiseGameScreen(gamingState = GamingState.Loading)

            onNodeWithTag("${GameDestination.HOME}-title")
                .assertDoesNotExist()
        }
    }

    @Test
    fun `expected list of games displays with expected details when outside game`() {
        composeTestRule.apply {
            initialiseGameScreen(gamingState = GamingState.OutOfGame)

            onNodeWithTag("${GameDestination.HOME}-title")
                .assertExists()
            onNodeWithTag("${GameDestination.HOME}-gameslist")
                .assertExists()

            onAllNodesWithTag("${GameDestination.HOME}-gamecard").apply {
                assertGamesInListHaveExpectedData()
            }
        }
    }

    @Test
    fun `enter expected game via dialog`() {
        composeTestRule.apply {
            val gameRepository = ControllableGameRepository()
            val viewModel = GameViewModel(GameStateUseCase(gameRepository), gameRepository)
            var actualCurrentGame = CurrentGame.EMPTY

            initialiseGameScreen(
                viewModel,
                onEnterGame = {
                    actualCurrentGame = it
                }
            )

            openDialogForEnteringFirstGame()

            // Dialog exists, and so enter game.
            onNodeWithTag("${GameDestination.HOME}-enter-alert")
                .assertExists()
            onNodeWithTag("${GameDestination.HOME}-enter-alert-confirm")
                .assertExists()
                .performClick()

            // In expected game screen.
            onNodeWithTag("${GameDestination.HOME}-enter-alert")
                .assertDoesNotExist()
            onNodeWithTag("${GameDestination.HOME}-gamecard")
                .assertDoesNotExist()
            onNodeWithTag("${GameDestination.HOME}-current-game-column")
                .assertExists()
            onNodeWithTag("${GameDestination.HOME}-current-game-title")
                .assertTextContains(dummyGames().first().toCurrentGame().name)

            assertEquals(dummyGames().first().toCurrentGame(), actualCurrentGame)
        }
    }

    @Test
    fun `exit a game so that a list of games displays again`() {
        composeTestRule.apply {
            val gameRepository = ControllableGameRepository(initialCurrentGame = dummyGames().first().toCurrentGame())
            val viewModel = GameViewModel(GameStateUseCase(gameRepository), gameRepository)
            var actualCurrentGame = dummyGames().first().toCurrentGame()

            initialiseGameScreen(
                viewModel,
                onExitGame = {
                    actualCurrentGame = it
                }
            )

            // Ensure current state is within game.
            onNodeWithTag("${GameDestination.HOME}-gamecard")
                .assertDoesNotExist()
            onNodeWithTag("${GameDestination.HOME}-current-game-column")
                .assertExists()
            onNodeWithTag("${GameDestination.HOME}-current-game-title")
                .assertTextContains(dummyGames().first().toCurrentGame().name)

            // Press button to exit game.
            onNodeWithTag("${GameDestination.HOME}-exit-current-game")
                .assertHasClickAction()
                .performClick()

            // Now in out of game screen.
            onNodeWithTag("${GameDestination.HOME}-title")
                .assertExists()
            onNodeWithTag("${GameDestination.HOME}-gameslist")
                .assertExists()
            onNodeWithTag("${GameDestination.HOME}-enter-alert")
                .assertDoesNotExist()
            onAllNodesWithTag("${GameDestination.HOME}-gamecard")
                .onLast()
                .assertExists()

            assertEquals(CurrentGame.EMPTY, actualCurrentGame)
        }
    }

    @Test
    fun `dismiss an enter game dialog to remove it`() {
        composeTestRule.apply {
            val gameRepository = ControllableGameRepository()
            val viewModel = GameViewModel(GameStateUseCase(gameRepository), gameRepository)
            var actualCurrentGame = CurrentGame.EMPTY

            initialiseGameScreen(
                viewModel,
                onEnterGame = {
                    actualCurrentGame = it
                }
            )

            openDialogForEnteringFirstGame()

            // Dialog exists, but dismiss it.
            onNodeWithTag("${GameDestination.HOME}-enter-alert")
                .assertExists()
            onNodeWithTag("${GameDestination.HOME}-enter-alert-reject")
                .assertExists()
                .performClick()

            // Still in original out of game screen.
            onNodeWithTag("${GameDestination.HOME}-title")
                .assertExists()
            onNodeWithTag("${GameDestination.HOME}-gameslist")
                .assertExists()
            onNodeWithTag("${GameDestination.HOME}-enter-alert")
                .assertDoesNotExist()
            onAllNodesWithTag("${GameDestination.HOME}-gamecard")
                .onLast()
                .assertExists()

            assertEquals(CurrentGame.EMPTY, actualCurrentGame)
        }
    }

    private fun SemanticsNodeInteractionCollection.assertGamesInListHaveExpectedData() {
        fetchSemanticsNodes().forEachIndexed { index, _ ->
            val currentCard = get(index)
            currentCard.apply {
                hasAnyChild(hasText(dummyGames()[index].name))
                hasAnyChild(hasText(dummyGames()[index].description))
                hasAnyChild(hasTestTag("${GameDestination.HOME}-game-enter-button"))
            }
        }
    }

    private fun initialiseGameScreen(gamingState: GamingState) {
        composeTestRule.setContent {
            GameScreen(
                games = dummyGames(),
                gamingState = gamingState
            )
        }
    }

    private fun initialiseGameScreen(
        viewModel: GameViewModel,
        onEnterGame: (CurrentGame) -> Unit = {},
        onExitGame: (CurrentGame) -> Unit = {}
    ) {
        composeTestRule.setContent {
            GameScreen(
                games = dummyGames(),
                gamingState = viewModel.gamingState.collectAsStateWithLifecycle().value,
                onEnterGame = {
                    onEnterGame(it)
                    viewModel.enterGame(it)
                },
                onExitGame = {
                    viewModel.exitGame()
                    onExitGame(CurrentGame.EMPTY)
                }
            )
        }
    }

    private fun AndroidComposeTestRule<ActivityScenarioRule<ComponentActivity>, ComponentActivity>.openDialogForEnteringFirstGame() {
        val targetCard = onAllNodesWithTag("${GameDestination.HOME}-gamecard")
            .filter(
                hasAnyChild(
                    hasText(dummyGames().first().name, substring = true)
                )
            )
            .assertCountEquals(1)
            .onFirst()
        val enterButtonForTargetCard = targetCard.onChildren()
            .filter(
                hasTestTag("${GameDestination.HOME}-game-enter-button")
            )
            .assertCountEquals(1)
            .onFirst()

        enterButtonForTargetCard
            .assertHasClickAction()
            .performClick()
    }

    private fun dummyGames() = listOf(
        Game("abc", "My First Game", "It is the first one."),
        Game("def", "My Second Game", "It is the second one."),
        Game("ghi", "My Third Game", "It is the third one.")
    )

}
package com.carkzis.ananke.ui

import androidx.activity.ComponentActivity
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
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
import com.carkzis.ananke.data.model.CurrentGame
import com.carkzis.ananke.data.model.Game
import com.carkzis.ananke.data.model.toCurrentGame
import com.carkzis.ananke.navigation.GameDestination
import com.carkzis.ananke.testdoubles.ControllableGameRepository
import com.carkzis.ananke.testdoubles.ControllableTeamRepository
import com.carkzis.ananke.testdoubles.ControllableYouRepository
import com.carkzis.ananke.ui.screens.game.EnterGameFailedException
import com.carkzis.ananke.ui.screens.game.ExitGameFailedException
import com.carkzis.ananke.ui.screens.game.GameRoute
import com.carkzis.ananke.ui.screens.game.GameScreen
import com.carkzis.ananke.ui.screens.game.GameViewModel
import com.carkzis.ananke.ui.screens.game.GamingState
import com.carkzis.ananke.utils.CleanUpCharactersAndTeamMembersUseCase
import com.carkzis.ananke.utils.DeletableGameUseCase
import com.carkzis.ananke.utils.GameStateUseCase
import com.carkzis.ananke.utils.OnboardUserUseCase
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
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

    private var snackbarHostState: SnackbarHostState? = null
    private val onboardUseCase = OnboardUserUseCase(ControllableYouRepository())
    private val deletableGameUseCase = DeletableGameUseCase(ControllableYouRepository())

    private val cleanUpCharactersAndTeamMembersUseCase = CleanUpCharactersAndTeamMembersUseCase(
        ControllableYouRepository(),
        ControllableTeamRepository()
    )

    @After
    fun tearDown() {
        snackbarHostState = null
    }

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

            val viewModel = GameViewModel(
                GameStateUseCase(gameRepository),
                onboardUseCase,
                deletableGameUseCase,
                cleanUpCharactersAndTeamMembersUseCase,
                gameRepository,
            )
            var actualCurrentGame = CurrentGame.EMPTY

            initialiseGameScreen(
                viewModel,
                onEnterGame = {
                    actualCurrentGame = it
                }
            )

            openDialogForEnteringFirstGame()
            enterGameThroughDialog()

            onNodeWithTag("${GameDestination.HOME}-enter-alert")
                .assertDoesNotExist()
            assertCurrentStateIsWithinGame()

            assertEquals(dummyGames().first().toCurrentGame(), actualCurrentGame)
        }
    }

    @Test
    fun `exit a game so that a list of games displays again`() {
        composeTestRule.apply {
            val gameRepository = ControllableGameRepository(initialCurrentGame = dummyGames().first().toCurrentGame())
            val viewModel = GameViewModel(
                GameStateUseCase(gameRepository),
                onboardUseCase,
                deletableGameUseCase,
                cleanUpCharactersAndTeamMembersUseCase,
                gameRepository
            )
            var actualCurrentGame = dummyGames().first().toCurrentGame()

            initialiseGameScreen(
                viewModel,
                onExitGame = {
                    actualCurrentGame = it
                }
            )

            assertCurrentStateIsWithinGame()

            onNodeWithTag("${GameDestination.HOME}-exit-current-game")
                .assertHasClickAction()
                .performClick()

            assertCurrentStateIsOutOfGame()

            assertEquals(CurrentGame.EMPTY, actualCurrentGame)
        }
    }


    @Test
    fun `dismiss an enter game dialog to remove it`() {
        composeTestRule.apply {
            val gameRepository = ControllableGameRepository()
            val viewModel = GameViewModel(
                GameStateUseCase(gameRepository),
                onboardUseCase,
                deletableGameUseCase,
                cleanUpCharactersAndTeamMembersUseCase,
                gameRepository
            )
            var actualCurrentGame = CurrentGame.EMPTY

            initialiseGameScreen(
                viewModel,
                onEnterGame = {
                    actualCurrentGame = it
                }
            )

            openDialogForEnteringFirstGame()
            dismissDialog()

            assertCurrentStateIsOutOfGame()

            assertEquals(CurrentGame.EMPTY, actualCurrentGame)
        }
    }

    @Test
    fun `failing to enter game results in dialog disappearing and snackbar displaying`() {
        composeTestRule.apply {
            val gameRepository = ControllableGameRepository(initialGames = dummyGames()).apply {
                ENTRY_GENERIC_FAIL = true
            }
            val viewModel = GameViewModel(
                GameStateUseCase(gameRepository),
                onboardUseCase,
                deletableGameUseCase,
                cleanUpCharactersAndTeamMembersUseCase,
                gameRepository
            )

            initialiseGameScreenViaGameRoute(viewModel)

            openDialogForEnteringFirstGame()
            enterGameThroughDialog()

            assertCurrentStateIsOutOfGame()

            assertSnapshotHasExpectedMessage(expectedSnackbarText = EnterGameFailedException().message)
        }
    }

    @Test
    fun `failing to exit a game results in dialog disappearing and snackbar displaying`() {
        composeTestRule.apply {
            val gameRepository = ControllableGameRepository(initialCurrentGame = dummyGames().first().toCurrentGame()).apply {
                FAIL_EXIT = true
            }
            val viewModel = GameViewModel(
                GameStateUseCase(gameRepository),
                onboardUseCase,
                deletableGameUseCase,
                cleanUpCharactersAndTeamMembersUseCase,
                gameRepository
            )

            initialiseGameScreenViaGameRoute(viewModel)

            assertCurrentStateIsWithinGame()

            onNodeWithTag("${GameDestination.HOME}-exit-current-game")
                .assertHasClickAction()
                .performClick()

            onNodeWithTag("${GameDestination.HOME}-enter-alert")
                .assertDoesNotExist()
            assertCurrentStateIsWithinGame()

            assertSnapshotHasExpectedMessage(expectedSnackbarText = ExitGameFailedException().message)
        }
    }

    @Test
    fun `delete game button inactive when no deletable games`() {

    }

    @Test
    fun `clicking delete a game button opens a dialogue with deletable games`() {

    }

    @Test
    fun `can exit initial deleting a game dialogue`() {

    }

    @Test
    fun `clicking delete on a specific game in the delete a game dialogue opens new dialogue`() {

    }

    @Test
    fun `can exit delete a game dialogue returning to initial dialogue`() {

    }

    @Test
    fun `deleting a game exits all delete game dialogues with game removed`() {

    }

    private fun initialiseGameScreenViaGameRoute(
        viewModel: GameViewModel
    ) {
        composeTestRule.setContent {
            snackbarHostState = remember { SnackbarHostState() }
            GameRoute(
                viewModel = viewModel,
                onShowSnackbar = { message ->
                    snackbarHostState?.showSnackbar(
                        message = message, duration = SnackbarDuration.Short
                    ) == SnackbarResult.Dismissed
                }
            )
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
                deletableGames = listOf(),
                gamingState = gamingState
            )
        }
    }

    private fun initialiseGameScreen(
        viewModel: GameViewModel,
        onEnterGame: (CurrentGame) -> Unit = {},
        onExitGame: (CurrentGame) -> Unit = {},
    ) {
        composeTestRule.setContent {
            GameScreen(
                games = dummyGames(),
                deletableGames = listOf(),
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

    private fun AndroidComposeTestRule<ActivityScenarioRule<ComponentActivity>, ComponentActivity>.enterGameThroughDialog() {
        onNodeWithTag("${GameDestination.HOME}-enter-alert")
            .assertExists()
        onNodeWithTag("${GameDestination.HOME}-enter-alert-confirm")
            .assertExists()
            .performClick()
    }

    private fun AndroidComposeTestRule<ActivityScenarioRule<ComponentActivity>, ComponentActivity>.dismissDialog() {
        onNodeWithTag("${GameDestination.HOME}-enter-alert")
            .assertExists()
        onNodeWithTag("${GameDestination.HOME}-enter-alert-reject")
            .assertExists()
            .performClick()
    }

    private fun AndroidComposeTestRule<ActivityScenarioRule<ComponentActivity>, ComponentActivity>.assertCurrentStateIsWithinGame() {
        onNodeWithTag("${GameDestination.HOME}-gamecard")
            .assertDoesNotExist()
        onNodeWithTag("${GameDestination.HOME}-current-game-column")
            .assertExists()
        onNodeWithTag("${GameDestination.HOME}-current-game-title")
            .assertTextContains(dummyGames().first().toCurrentGame().name)
    }

    private fun AndroidComposeTestRule<ActivityScenarioRule<ComponentActivity>, ComponentActivity>.assertCurrentStateIsOutOfGame() {
        onNodeWithTag("${GameDestination.HOME}-title")
            .assertExists()
        onNodeWithTag("${GameDestination.HOME}-gameslist")
            .assertExists()
        onNodeWithTag("${GameDestination.HOME}-enter-alert")
            .assertDoesNotExist()
        onAllNodesWithTag("${GameDestination.HOME}-gamecard")
            .onLast()
            .assertExists()
    }

    private fun assertSnapshotHasExpectedMessage(expectedSnackbarText: String) {
        runBlocking {
            val actualSnackbarText = snapshotFlow { snackbarHostState?.currentSnackbarData }
                .first()?.visuals?.message
            assertEquals(expectedSnackbarText, actualSnackbarText)
        }
    }

    private fun dummyGames() = listOf(
        Game("1", "My First Game", "It is the first one.", "1"),
        Game("2", "My Second Game", "It is the second one.", "2"),
        Game("3", "My Third Game", "It is the third one.", "3")
    )

}
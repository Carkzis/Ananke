package com.carkzis.ananke.ui

import androidx.activity.ComponentActivity
import androidx.compose.runtime.getValue
import androidx.compose.ui.test.SemanticsNodeInteractionCollection
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.filter
import androidx.compose.ui.test.hasAnyChild
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onChildren
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.carkzis.ananke.data.CurrentGame
import com.carkzis.ananke.data.User
import com.carkzis.ananke.data.toGame
import com.carkzis.ananke.navigation.AnankeDestination
import com.carkzis.ananke.testdoubles.ControllableGameRepository
import com.carkzis.ananke.testdoubles.ControllableTeamRepository
import com.carkzis.ananke.ui.screens.team.TeamRoute
import com.carkzis.ananke.ui.screens.team.TeamScreen
import com.carkzis.ananke.ui.screens.team.TeamViewModel
import com.carkzis.ananke.utils.CheckGameExistsUseCase
import com.carkzis.ananke.utils.GameStateUseCase
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@OptIn(ExperimentalCoroutinesApi::class)
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
        composeTestRule.apply {
            val gameRepository = ControllableGameRepository()
            val teamRepository = ControllableTeamRepository()
            val viewModel = TeamViewModel(GameStateUseCase(gameRepository), CheckGameExistsUseCase(gameRepository), teamRepository)

            val currentGame = CurrentGame("1", "A Game", "A Description")
            gameRepository.emitCurrentGame(currentGame)

            composeTestRule.setContent {
                val gameState by viewModel.gamingState.collectAsStateWithLifecycle()
                val actualCurrentGame = viewModel
                    .currentGame
                    .collectAsStateWithLifecycle(initialValue = CurrentGame.EMPTY)
                    .value
                TeamScreen(
                    currentGame = actualCurrentGame,
                    gamingState = gameState,
                )
            }

            onNodeWithTag("${AnankeDestination.TEAM}-current-game")
                .assertTextContains(currentGame.name)
        }
    }

    @Test
    fun `redirects to game screen if enter team screen when out of game`() {
        composeTestRule.apply {
            var redirected = false
            val gameRepository = ControllableGameRepository(initialCurrentGame = CurrentGame.EMPTY)
            val teamRepository = ControllableTeamRepository()
            val viewModel = TeamViewModel(GameStateUseCase(gameRepository), CheckGameExistsUseCase(gameRepository), teamRepository)

            composeTestRule.setContent {
                TeamRoute(
                    viewModel = viewModel,
                    onOutOfGame = { redirected = true },
                )
            }

            assertTrue(redirected)
        }
    }

    @Test
    fun `redirects to game screen from team screen when going out of game`() = runTest {
        composeTestRule.apply {
            var redirected = false
            val game = CurrentGame("123")
            val gameRepository = ControllableGameRepository(initialCurrentGame = game)
            val teamRepository = ControllableTeamRepository()
            val viewModel = TeamViewModel(GameStateUseCase(gameRepository), CheckGameExistsUseCase(gameRepository), teamRepository)

            composeTestRule.setContent {
                TeamRoute(
                    viewModel = viewModel,
                    onOutOfGame = { redirected = true },
                )
            }

            assertFalse(redirected)

            gameRepository.removeCurrentGame()

            // IMPORTANT: SemanticsNode assertion is required in tests in order to cause recomposition.
            onNodeWithTag("${AnankeDestination.TEAM}-title")
                .assertDoesNotExist()
            assertTrue(redirected)
        }
    }

    @Test
    fun `displays list of potential team members`() = runTest {
        composeTestRule.apply {
            val game = CurrentGame("123")
            val gameRepository = ControllableGameRepository()
            val teamRepository = ControllableTeamRepository()
            val viewModel = TeamViewModel(GameStateUseCase(gameRepository), CheckGameExistsUseCase(gameRepository), teamRepository)

            gameRepository.emitCurrentGame(game)

            composeTestRule.setContent {
                val gameState by viewModel.gamingState.collectAsStateWithLifecycle()
                TeamScreen(
                    currentGame = game,
                    gamingState = gameState,
                    users = dummyUsers()
                )
            }

            onNodeWithTag("${AnankeDestination.TEAM}-team-column")
                .assertExists()
            onNodeWithTag("${AnankeDestination.TEAM}-users-title")
                .assertExists()

            onAllNodesWithTag("${AnankeDestination.TEAM}-user-card").apply {
                assertUsersInListHaveExpectedData(dummyUsers())
            }
        }
    }

    @Test
    fun `adds a team member from the potential list to the current list`() = runTest {
        composeTestRule.apply {
            val game = CurrentGame("123")
            val gameRepository = ControllableGameRepository()
            val teamRepository = ControllableTeamRepository()
            val viewModel = TeamViewModel(GameStateUseCase(gameRepository), CheckGameExistsUseCase(gameRepository), teamRepository)

            gameRepository.emitCurrentGame(game)
            teamRepository.emitUsers(dummyUsers())

            composeTestRule.setContent {
                TeamRoute(
                    viewModel = viewModel,
                    onOutOfGame = {}
                )
            }

            onNodeWithTag("${AnankeDestination.TEAM}-team-column")
                .assertExists()
            onNodeWithTag("${AnankeDestination.TEAM}-team-member-title")
                .assertExists()

            val targetCard = onAllNodesWithTag("${AnankeDestination.TEAM}-user-card")
                .filter(
                    hasAnyChild(
                        hasText(dummyUsers().first().name, substring = true)
                    )
                )
                .assertCountEquals(1)
                .onFirst()
            val addTeamMemberButtonForTargetCard = targetCard.onChildren()
                .filter(
                    hasTestTag("${AnankeDestination.TEAM}-add-user-button")
                )
                .assertCountEquals(1)
                .onFirst()

            addTeamMemberButtonForTargetCard
                .assertHasClickAction()
                .performClick()

            gameRepository.emitGames(listOf(game.toGame()))

            onNodeWithTag("${AnankeDestination.TEAM}-tm-card")
                .assertExists()

            onAllNodesWithTag("${AnankeDestination.TEAM}-tm-card").apply {
                assertUsersInListHaveExpectedData(expectedUsers = listOf(dummyUsers().first()))
            }

            onAllNodesWithTag("${AnankeDestination.TEAM}-user-card").apply {
                assertUsersInListHaveExpectedData(expectedUsers = dummyUsers().drop(1))
            }
        }
    }

    private fun SemanticsNodeInteractionCollection.assertUsersInListHaveExpectedData(expectedUsers: List<User>) {
        fetchSemanticsNodes().forEachIndexed { index, _ ->
            val currentCard = get(index)
            currentCard.apply {
                hasAnyChild(hasText(expectedUsers[index].name))
            }
        }
    }

    private fun dummyUsers() = listOf(
        User(id = 1, name = "Zidun"),
        User(id = 2, name = "Vivu"),
        User(id = 3, name = "Steinur"),
        User(id = 4, name = "Garnut")
    )
}
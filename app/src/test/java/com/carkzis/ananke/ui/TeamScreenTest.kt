package com.carkzis.ananke.ui

import androidx.activity.ComponentActivity
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.test.SemanticsNodeInteractionCollection
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
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
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.carkzis.ananke.data.model.CurrentGame
import com.carkzis.ananke.data.model.User
import com.carkzis.ananke.data.model.toGame
import com.carkzis.ananke.navigation.AnankeDestination
import com.carkzis.ananke.testdoubles.ControllableGameRepository
import com.carkzis.ananke.testdoubles.ControllableTeamRepository
import com.carkzis.ananke.testdoubles.ControllableYouRepository
import com.carkzis.ananke.ui.screens.team.TeamRoute
import com.carkzis.ananke.ui.screens.team.TeamScreen
import com.carkzis.ananke.ui.screens.team.TeamViewModel
import com.carkzis.ananke.ui.screens.team.TooManyUsersInTeamException
import com.carkzis.ananke.utils.AddCurrentUserToTheirEmptyGameUseCase
import com.carkzis.ananke.utils.AddTeamMemberUseCase
import com.carkzis.ananke.utils.CheckGameExistsUseCase
import com.carkzis.ananke.utils.GameStateUseCase
import com.carkzis.ananke.utils.UserCharacterUseCase
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
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

    private lateinit var gameRepository: ControllableGameRepository
    private lateinit var teamRepository: ControllableTeamRepository
    private lateinit var youRepository: ControllableYouRepository

    @Before
    fun setUp() {
        gameRepository = ControllableGameRepository()
        teamRepository = ControllableTeamRepository()
        youRepository = ControllableYouRepository()
    }

    @Test
    fun `display title and current game name`() {
        composeTestRule.apply {
            gameRepository = ControllableGameRepository()
            val viewModel = teamViewModel()

            val currentGame = CurrentGame("1", "A Game", "A Description")
            gameRepository.emitCurrentGame(currentGame)

            initialiseTeamScreen(viewModel)

            onNodeWithTag("${AnankeDestination.TEAM}-title")
                .assertExists()
            onNodeWithTag("${AnankeDestination.TEAM}-current-game")
                .assertTextContains(currentGame.name)
        }
    }

    @Test
    fun `redirects to game screen if enter team screen when out of game`() {
        composeTestRule.apply {
            var redirected = false
            val viewModel = teamViewModel()

            initialiseTeamScreenViaTeamRoute(
                viewModel,
                onOutOfGame = { redirected = true }
            )

            assertTrue(redirected)
        }
    }

    @Test
    fun `redirects to game screen from team screen when going out of game`() = runTest {
        composeTestRule.apply {
            gameRepository = ControllableGameRepository(initialCurrentGame = CurrentGame("123"))
            val viewModel = teamViewModel()

            var redirected = false
            initialiseTeamScreenViaTeamRoute(
                viewModel,
                onOutOfGame = { redirected = true }
            )

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
            val viewModel = teamViewModel()

            val game = CurrentGame("123")
            gameRepository.emitCurrentGame(game)

            initialiseTeamScreen(viewModel)

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
    fun `displays message when no team members in game`() = runTest {
        composeTestRule.apply {
            val viewModel = teamViewModel()

            val game = CurrentGame("123")
            gameRepository.emitCurrentGame(game)

            initialiseTeamScreen(viewModel)

            onNodeWithTag("${AnankeDestination.TEAM}-no-team-members-text")
                .assertIsDisplayed()
        }
    }

    @Test
    fun `adds a team member from the potential list to the current list`() = runTest {
        composeTestRule.apply {
            val viewModel = teamViewModel()

            val game = CurrentGame("123")
            gameRepository.emitCurrentGame(game)
            teamRepository.emitUsers(dummyUsers())

            initialiseTeamScreenViaTeamRoute(viewModel)

            onNodeWithTag("${AnankeDestination.TEAM}-team-column")
                .assertExists()
            onNodeWithTag("${AnankeDestination.TEAM}-team-member-title")
                .assertExists()

            addHighestUserInListToTeam(
                onCompletion = {
                    gameRepository.emitGames(listOf(game.toGame()))
                }
            )

            onNodeWithTag("${AnankeDestination.TEAM}-tm-card")
                .assertIsDisplayed()
            onNodeWithTag("${AnankeDestination.TEAM}-no-team-members-text")
                .assertDoesNotExist()
            onAllNodesWithTag("${AnankeDestination.TEAM}-tm-card").apply {
                assertUsersInListHaveExpectedData(expectedUsers = listOf(dummyUsers().first()))
            }

            onAllNodesWithTag("${AnankeDestination.TEAM}-user-card").apply {
                assertUsersInListHaveExpectedData(expectedUsers = dummyUsers().drop(1))
            }
        }
    }

    @Test
    fun `when unable to add more team members a snackbar appears`() = runTest {
        var snackbarHostState: SnackbarHostState? = null
        val viewModel = teamViewModel()
        val game = CurrentGame("123")
        val teamLimit = 0

        composeTestRule.setContent {
            snackbarHostState = remember { SnackbarHostState() }

            gameRepository.emitCurrentGame(game)
            teamRepository.emitUsers(dummyUsers())
            teamRepository.limit = teamLimit

            TeamRoute(
                viewModel = viewModel,
                onOutOfGame = {},
                onShowSnackbar = { message ->
                    snackbarHostState?.showSnackbar(
                        message = message, duration = SnackbarDuration.Short
                    ) == SnackbarResult.Dismissed
                }
            )
        }

        composeTestRule.apply {
            addHighestUserInListToTeam(
                onCompletion = {
                    gameRepository.emitGames(listOf(game.toGame()))
                }
            )

            runBlocking {
                val actualSnackbarText = snapshotFlow { snackbarHostState?.currentSnackbarData }
                    .first()?.visuals?.message
                val expectedSnackbarText = TooManyUsersInTeamException(teamLimit).message
                assertEquals(expectedSnackbarText, actualSnackbarText)
            }
        }
    }

    @Test
    fun `view and dismiss dialogue of a user`() = runTest {
        composeTestRule.apply {
            val viewModel = teamViewModel()

            val game = CurrentGame("123")
            gameRepository.emitCurrentGame(game)
            teamRepository.emitUsers(dummyUsers())

            initialiseTeamScreenViaTeamRoute(viewModel)

            onAllNodesWithTag("${AnankeDestination.TEAM}-user-name")
                .onFirst()
                .assertHasClickAction()
                .performClick()

            onNodeWithTag("${AnankeDestination.TEAM}-user-dialogue")
                .assertIsDisplayed()

            onNodeWithTag("${AnankeDestination.TEAM}-user-dialogue-close-button", useUnmergedTree = true)
                .performClick()

            onNodeWithTag("${AnankeDestination.TEAM}-user-dialogue")
                .assertIsNotDisplayed()
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

    private fun AndroidComposeTestRule<ActivityScenarioRule<ComponentActivity>, ComponentActivity>.addHighestUserInListToTeam(onCompletion: () -> Unit) {
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

        onCompletion()
    }

    private fun initialiseTeamScreen(viewModel: TeamViewModel) {
        composeTestRule.setContent {
            val gamingState by viewModel.gamingState.collectAsStateWithLifecycle()
            val actualCurrentGame = viewModel
                .currentGame
                .collectAsStateWithLifecycle(initialValue = CurrentGame.EMPTY)
                .value
            TeamScreen(
                currentGame = actualCurrentGame,
                gamingState = gamingState,
                users = dummyUsers()
            )
        }
    }

    private fun initialiseTeamScreenViaTeamRoute(
        viewModel: TeamViewModel,
        onOutOfGame: () -> Unit = {},
        onShowSnackbar: suspend (String) -> Boolean = { false }
    ) {
        composeTestRule.setContent {
            TeamRoute(
                viewModel = viewModel,
                onOutOfGame = onOutOfGame,
                onShowSnackbar = onShowSnackbar
            )
        }
    }

    private fun teamViewModel(): TeamViewModel {
        val viewModel = TeamViewModel(
            GameStateUseCase(gameRepository),
            AddCurrentUserToTheirEmptyGameUseCase(teamRepository, youRepository),
            AddTeamMemberUseCase(teamRepository, youRepository),
            UserCharacterUseCase(youRepository),
            CheckGameExistsUseCase(gameRepository),
            teamRepository
        )
        return viewModel
    }

    private fun dummyUsers() = listOf(
        User(id = 1, name = "Zidun"),
        User(id = 2, name = "Vivu"),
        User(id = 3, name = "Steinur"),
        User(id = 4, name = "Garnut")
    )
}
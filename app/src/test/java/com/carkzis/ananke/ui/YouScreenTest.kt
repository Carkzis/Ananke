package com.carkzis.ananke.ui

import androidx.activity.ComponentActivity
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.carkzis.ananke.data.model.CurrentGame
import com.carkzis.ananke.navigation.AnankeDestination
import com.carkzis.ananke.testdoubles.ControllableGameRepository
import com.carkzis.ananke.testdoubles.ControllableYouRepository
import com.carkzis.ananke.ui.screens.you.YouRoute
import com.carkzis.ananke.ui.screens.you.YouScreen
import com.carkzis.ananke.ui.screens.you.YouUiState
import com.carkzis.ananke.ui.screens.you.YouValidatorFailure
import com.carkzis.ananke.ui.screens.you.YouViewModel
import com.carkzis.ananke.utils.GameStateUseCase
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@HiltAndroidTest
@RunWith(RobolectricTestRunner::class)
@Config(application = HiltTestApplication::class)
class YouScreenTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun `display current game name`() {
        val gameRepository = ControllableGameRepository()
        val controllableYouRepository = ControllableYouRepository()
        val viewModel = YouViewModel(GameStateUseCase(gameRepository), controllableYouRepository)

        val currentGame = CurrentGame("1", "A Game", "A Description")
        gameRepository.emitCurrentGame(currentGame)

        composeTestRule.apply {
            composeTestRule.setContent {
                val gameState by viewModel.gamingState.collectAsStateWithLifecycle()
                val actualCurrentGame = viewModel
                    .uiState
                    .collectAsStateWithLifecycle(initialValue = YouUiState.EMPTY)
                    .value
                    .currentGame
                YouScreen(
                    currentGame = actualCurrentGame,
                    gamingState = gameState,
                    onTitleValueChanged = { _, _ -> },
                    onBioValueChanged = { _, _ -> },
                    characterName = "",
                    characterBio = "",
                    onEnableEditCharacterName = {},
                    onEnableEditCharacterBio = {},
                    onConfirmCharacterNameChange = {},
                    onConfirmCharacterBioChange = {},
                    onCancelEdit = {},
                )
            }

            onNodeWithTag("${AnankeDestination.YOU}-current-game")
                .assertTextContains(currentGame.name)
        }
    }

    @Test
    fun `redirects to game screen when out of game`() = runTest {
        composeTestRule.apply {
            var redirected = false
            val gameRepository = ControllableGameRepository(initialCurrentGame = CurrentGame.EMPTY)
            val viewModel = YouViewModel(GameStateUseCase(gameRepository), youRepository = ControllableYouRepository())

            composeTestRule.setContent {
                YouRoute(
                    viewModel = viewModel,
                    onOutOfGame = {
                        redirected = true
                                  },
                    onShowSnackbar = { true }
                )
            }

            assertTrue(redirected)
        }
    }

    @Test
    fun `pressing edit causes cancel and confirm buttons to appear`() = runTest {
        composeTestRule.apply {
            val viewModel = setUpViewModel()

            composeTestRule.setContent {
                YouRoute(
                    viewModel = viewModel,
                    onOutOfGame = {},
                    onShowSnackbar = { true }
                )
            }

            val attributeType = "name"
            onNodeWithTag("${AnankeDestination.YOU}-edit-$attributeType-button").performClick()

            onNodeWithTag("${AnankeDestination.YOU}-confirm-$attributeType-button").assertExists()
            onNodeWithTag("${AnankeDestination.YOU}-cancel-$attributeType-button").assertExists()

            onNodeWithTag("${AnankeDestination.YOU}-edit-$attributeType-button").assertDoesNotExist()
        }
    }

    @Test
    fun `pressing confirm causes edit button to appear`() {
        composeTestRule.apply {
            val viewModel = setUpViewModel()

            composeTestRule.setContent {
                YouRoute(
                    viewModel = viewModel,
                    onOutOfGame = {},
                    onShowSnackbar = { true }
                )
            }

            val attributeType = "name"

            onNodeWithTag("${AnankeDestination.YOU}-edit-$attributeType-button").performClick()
            onNodeWithTag("${AnankeDestination.YOU}-confirm-$attributeType-button").performClick()

            onNodeWithTag("${AnankeDestination.YOU}-edit-$attributeType-button").assertExists()

            onNodeWithTag("${AnankeDestination.YOU}-confirm-$attributeType-button").assertDoesNotExist()
            onNodeWithTag("${AnankeDestination.YOU}-cancel-$attributeType-button").assertDoesNotExist()
        }
    }

    @Test
    @Config(sdk = [33])
    fun `pressing cancel causes edit button to appear`() {
        composeTestRule.apply {
            val viewModel = setUpViewModel()

            composeTestRule.setContent {
                YouRoute(
                    viewModel = viewModel,
                    onOutOfGame = {},
                    onShowSnackbar = { true }
                )
            }

            val attributeType = "name"

            onNodeWithTag("${AnankeDestination.YOU}-edit-$attributeType-button").performClick()
            onNodeWithTag("${AnankeDestination.YOU}-cancel-$attributeType-button").performClick()

            onNodeWithTag("${AnankeDestination.YOU}-edit-$attributeType-button").assertExists()

            onNodeWithTag("${AnankeDestination.YOU}-confirm-$attributeType-button").assertDoesNotExist()
            onNodeWithTag("${AnankeDestination.YOU}-cancel-$attributeType-button").assertDoesNotExist()
        }
    }

    @Test
    fun `text boxes can be edited when edit mode enabled`() {
        composeTestRule.apply {
            val viewModel = setUpViewModel()

            composeTestRule.setContent {
                YouRoute(
                    viewModel = viewModel,
                    onOutOfGame = {},
                    onShowSnackbar = { true }
                )
            }

            val attributeType = "name"

            onNodeWithTag("${AnankeDestination.YOU}-edit-$attributeType-button").performClick()
            onNodeWithTag("${AnankeDestination.YOU}-character-$attributeType")
                .performTextClearance()
            onNodeWithTag("${AnankeDestination.YOU}-character-$attributeType")
                .performTextInput("Something")

            onNodeWithTag("${AnankeDestination.YOU}-character-$attributeType", useUnmergedTree = true)
                .assertTextContains("Something")
        }
    }

    @Test
    fun `text boxes cannot be edited when edit mode disabled`() {
        composeTestRule.apply {
            val viewModel = setUpViewModel()

            composeTestRule.setContent {
                YouRoute(
                    viewModel = viewModel,
                    onOutOfGame = {},
                    onShowSnackbar = { true }
                )
            }

            val attributeType = "name"

            onNodeWithTag("${AnankeDestination.YOU}-character-$attributeType")
                .performTextClearance()

            assertTextFieldEmpty(attributeType)
        }
    }

    @Test
    fun `snack bar appears when expected`() {
        composeTestRule.apply {
            var snackbarHostState: SnackbarHostState? = null
            val viewModel = setUpViewModel()

            composeTestRule.setContent {
                snackbarHostState = remember { SnackbarHostState() }
                YouRoute(
                    viewModel = viewModel,
                    onOutOfGame = {},
                    onShowSnackbar = { message ->
                        snackbarHostState?.showSnackbar(
                            message = message, duration = SnackbarDuration.Short
                        ) == SnackbarResult.Dismissed
                    }
                )
            }

            val attributeType = "name"
            onNodeWithTag("${AnankeDestination.YOU}-edit-$attributeType-button").performClick()
            onNodeWithTag("${AnankeDestination.YOU}-character-$attributeType")
                .performTextClearance()
            onNodeWithTag("${AnankeDestination.YOU}-confirm-$attributeType-button").performClick()

            snackbarHostState?.assertSnackbarDisplays(YouValidatorFailure.NAME_TOO_SHORT.message)
        }
    }

    private fun setUpViewModel(): YouViewModel {
        val gameRepository = ControllableGameRepository()
        val viewModel = YouViewModel(
            GameStateUseCase(gameRepository),
            youRepository = ControllableYouRepository()
        )

        val currentGame = CurrentGame("1", "A Game", "A Description")
        gameRepository.emitCurrentGame(currentGame)

        return viewModel
    }

    private fun AndroidComposeTestRule<ActivityScenarioRule<ComponentActivity>, ComponentActivity>.assertTextFieldEmpty(
        attributeType: String
    ) {
        val textFieldNode = onNodeWithTag(
            "${AnankeDestination.YOU}-character-$attributeType",
            useUnmergedTree = true
        )
            .fetchSemanticsNode()
        for ((key, value) in textFieldNode.config) {
            if (key.name == "EditableText")
                assertTrue(value.toString().isNotEmpty())
        }
    }

    private fun SnackbarHostState.assertSnackbarDisplays(expectedSnackbarText: String) {
        runBlocking {
            val actualSnackbarText = snapshotFlow { this@assertSnackbarDisplays.currentSnackbarData }
                .first()?.visuals?.message
            Assert.assertEquals(expectedSnackbarText, actualSnackbarText)
        }
    }
}
package com.carkzis.ananke.ui

import androidx.activity.ComponentActivity
import androidx.compose.runtime.getValue
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.carkzis.ananke.data.model.CurrentGame
import com.carkzis.ananke.navigation.AnankeDestination
import com.carkzis.ananke.testdoubles.ControllableGameRepository
import com.carkzis.ananke.testdoubles.ControllableYouRepository
import com.carkzis.ananke.ui.screens.you.YouRoute
import com.carkzis.ananke.ui.screens.you.YouScreen
import com.carkzis.ananke.ui.screens.you.YouViewModel
import com.carkzis.ananke.utils.GameStateUseCase
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import junit.framework.TestCase
import kotlinx.coroutines.test.runTest
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

    private val attributeTypes = listOf("name", "bio")

    @Test
    fun `display current game name`() {
        val gameRepository = ControllableGameRepository()
        val viewModel = YouViewModel(GameStateUseCase(gameRepository), mock())

        val currentGame = CurrentGame("1", "A Game", "A Description")
        gameRepository.emitCurrentGame(currentGame)

        composeTestRule.apply {
            composeTestRule.setContent {
                val gameState by viewModel.gamingState.collectAsStateWithLifecycle()
                val actualCurrentGame = viewModel
                    .currentGame
                    .collectAsStateWithLifecycle(initialValue = CurrentGame.EMPTY)
                    .value
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

            TestCase.assertTrue(redirected)
        }
    }

    @Test
    fun `pressing edit causes cancel and confirm buttons to appear`() {
        composeTestRule.apply {
            val gameRepository = ControllableGameRepository()
            val viewModel = YouViewModel(GameStateUseCase(gameRepository), youRepository = ControllableYouRepository())

            val currentGame = CurrentGame("1", "A Game", "A Description")
            gameRepository.emitCurrentGame(currentGame)

            composeTestRule.setContent {
                YouRoute(
                    viewModel = viewModel,
                    onOutOfGame = {},
                    onShowSnackbar = { true }
                )
            }

            val attributeType = attributeTypes.random()
            onNodeWithTag("${AnankeDestination.YOU}-edit-$attributeType-button").performClick()
            onNodeWithTag("${AnankeDestination.YOU}-confirm-$attributeType-button").assertExists()
            onNodeWithTag("${AnankeDestination.YOU}-cancel-$attributeType-button").assertExists()
            onNodeWithTag("${AnankeDestination.YOU}-edit-$attributeType-button").assertDoesNotExist()
        }
    }

    @Test
    fun `pressing confirm causes edit button to appear`() {
        composeTestRule.apply {
            val gameRepository = ControllableGameRepository()
            val viewModel = YouViewModel(GameStateUseCase(gameRepository), youRepository = ControllableYouRepository())

            val currentGame = CurrentGame("1", "A Game", "A Description")
            gameRepository.emitCurrentGame(currentGame)

            composeTestRule.setContent {
                YouRoute(
                    viewModel = viewModel,
                    onOutOfGame = {},
                    onShowSnackbar = { true }
                )
            }

            val attributeType = attributeTypes.random()
            onNodeWithTag("${AnankeDestination.YOU}-edit-$attributeType-button").performClick()
            onNodeWithTag("${AnankeDestination.YOU}-confirm-$attributeType-button").performClick()
            onNodeWithTag("${AnankeDestination.YOU}-edit-$attributeType-button").assertExists()
            onNodeWithTag("${AnankeDestination.YOU}-confirm-$attributeType-button").assertDoesNotExist()
            onNodeWithTag("${AnankeDestination.YOU}-cancel-$attributeType-button").assertDoesNotExist()
        }
    }

    @Test
    fun `pressing cancel causes edit button to appear`() {
        composeTestRule.apply {
            val gameRepository = ControllableGameRepository()
            val viewModel = YouViewModel(GameStateUseCase(gameRepository), youRepository = ControllableYouRepository())

            val currentGame = CurrentGame("1", "A Game", "A Description")
            gameRepository.emitCurrentGame(currentGame)

            composeTestRule.setContent {
                YouRoute(
                    viewModel = viewModel,
                    onOutOfGame = {},
                    onShowSnackbar = { true }
                )
            }

            val attributeType = attributeTypes.random()
            onNodeWithTag("${AnankeDestination.YOU}-edit-$attributeType-button").performClick()
            onNodeWithTag("${AnankeDestination.YOU}-cancel-$attributeType-button").performClick()
            onNodeWithTag("${AnankeDestination.YOU}-edit-$attributeType-button").assertExists()
            onNodeWithTag("${AnankeDestination.YOU}-confirm-$attributeType-button").assertDoesNotExist()
            onNodeWithTag("${AnankeDestination.YOU}-cancel-$attributeType-button").assertDoesNotExist()
        }
    }


    @Test
    fun `text boxes can be edited when edit mode enabled`() {

    }

    @Test
    fun `text boxes cannot be edited when edit mode disabled`() {

    }

    @Test
    fun `snack bar appears when expected`() {

    }
}
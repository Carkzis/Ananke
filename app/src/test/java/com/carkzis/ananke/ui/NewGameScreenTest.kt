package com.carkzis.ananke.ui

import androidx.activity.ComponentActivity
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.carkzis.ananke.navigation.GameDestination
import com.carkzis.ananke.testdoubles.DummyGameRepository
import com.carkzis.ananke.ui.screens.nugame.NewGameRoute
import com.carkzis.ananke.ui.screens.nugame.NewGameScreen
import com.carkzis.ananke.ui.screens.nugame.NewGameValidatorFailure
import com.carkzis.ananke.ui.screens.nugame.NewGameViewModel
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@HiltAndroidTest
@RunWith(RobolectricTestRunner::class)
@Config(application = HiltTestApplication::class)
class NewGameScreenTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun `text box for new game title takes in typed characters`() {
        val viewModel = NewGameViewModel(DummyGameRepository())
        composeTestRule.setContent {
            NewGameScreen(
                gameTitle = viewModel.gameTitle.collectAsStateWithLifecycle().value,
                gameDescription = "",
                onTitleValueChanged = viewModel::updateGameTitle,
                onDescriptionValueChanged = {},
                onAttemptAddGameClick = {},
                onAddDummyGameClick = {},
                onAddGameSucceeds = {},
                onShowSnackbar = {}
            )
        }

        composeTestRule.apply {
            onNodeWithTag("${GameDestination.NEW}-game-title")
                .performClick()
                .performTextInput("A Game Title")
            onNodeWithTag("${GameDestination.NEW}-game-title")
                .assertTextContains("A Game Title")
        }
    }

    @Test
    fun `text box for new game description takes in typed characters`() {
        val viewModel = NewGameViewModel(DummyGameRepository())
        composeTestRule.setContent {
            NewGameScreen(
                gameTitle = "",
                gameDescription = viewModel.gameDescription.collectAsStateWithLifecycle().value,
                onTitleValueChanged = {},
                onDescriptionValueChanged = viewModel::updateGameDescription,
                onAttemptAddGameClick = {},
                onAddDummyGameClick = {},
                onAddGameSucceeds = {},
                onShowSnackbar = {}
            )
        }

        composeTestRule.apply {
            onNodeWithTag("${GameDestination.NEW}-game-description")
                .performClick()
                .performTextInput("A Game Description")
            onNodeWithTag("${GameDestination.NEW}-game-description")
                .assertTextContains("A Game Description")
        }
    }

    @Test
    fun `add new game results in redirect to game screen`() {
        var redirected = false
        composeTestRule.setContent {
            NewGameRoute(
                onAddGameClick = { redirected = true },
                viewModel = NewGameViewModel(DummyGameRepository()),
                onShowSnackbar = { false }
            )
        }

        composeTestRule.apply {
            onNodeWithTag("${GameDestination.NEW}-game-title")
                .performClick()
                .performTextInput("A Game Title")
            onNodeWithTag("${GameDestination.NEW}-game-description")
                .performClick()
                .performTextInput("A Game Description")
            onNodeWithTag("${GameDestination.NEW}-addnewgame-button", useUnmergedTree = true)
                .performClick()
            assertTrue(redirected)
        }
    }

    @Test
    fun `add invalid new game does not result redirect to game screen`() {
        var redirected = false
        composeTestRule.setContent {
            NewGameRoute(
                onAddGameClick = { redirected = true },
                viewModel = NewGameViewModel(DummyGameRepository()),
                onShowSnackbar = { false }
            )
        }

        composeTestRule.apply {
            onNodeWithTag("${GameDestination.NEW}-addnewgame-button", useUnmergedTree = true)
                .performClick()
            assertFalse(redirected)
        }
    }

    @Test
    fun `snackbar displays when try to add game with invalid input`() {
        var snackbarHostState: SnackbarHostState? = null
        composeTestRule.setContent {
            snackbarHostState = remember { SnackbarHostState() }
            NewGameRoute(
                onAddGameClick = {},
                viewModel = NewGameViewModel(DummyGameRepository()),
                onShowSnackbar = { message ->
                    snackbarHostState?.showSnackbar(
                        message = message, duration = SnackbarDuration.Short
                    ) == SnackbarResult.Dismissed
                }
            )
        }

        composeTestRule.apply {
            onNodeWithTag("${GameDestination.NEW}-addnewgame-button", useUnmergedTree = true)
                .performClick()
            runBlocking {
                val actualSnackbarText = snapshotFlow { snackbarHostState?.currentSnackbarData }
                    .first()?.visuals?.message
                val expectedSnackbarText = NewGameValidatorFailure.TITLE_EMPTY.message
                assertEquals(expectedSnackbarText, actualSnackbarText)
            }
        }
    }

}
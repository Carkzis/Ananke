package com.carkzis.ananke.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.carkzis.ananke.MainActivity
import com.carkzis.ananke.data.DEFAULT_TEAM_SIZE
import com.carkzis.ananke.data.model.CurrentGame
import com.carkzis.ananke.data.model.Game
import com.carkzis.ananke.data.repository.GameRepository
import com.carkzis.ananke.data.repository.TeamRepository
import com.carkzis.ananke.data.repository.YouRepository
import com.carkzis.ananke.di.DataModule
import com.carkzis.ananke.navigation.AnankeDestination
import com.carkzis.ananke.navigation.GameDestination
import com.carkzis.ananke.testdoubles.ControllableGameRepository
import com.carkzis.ananke.testdoubles.ControllableTeamRepository
import com.carkzis.ananke.testdoubles.ControllableYouRepository
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.apply

@HiltAndroidTest
@RunWith(RobolectricTestRunner::class)
@Config(application = HiltTestApplication::class)
@UninstallModules(DataModule::class)
class TopBarTest {

    @BindValue
    @JvmField
    val gameRepository: GameRepository = ControllableGameRepository()

    @BindValue
    @JvmField
    val teamRepository: TeamRepository = ControllableTeamRepository()

    @BindValue
    @JvmField
    val youRepository: YouRepository = ControllableYouRepository()

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun `game screen has search button enabled when not in game`() {
        composeTestRule.apply {
            onNodeWithTag("global-search-button")
                .assertIsDisplayed()
                .assertIsEnabled()
        }
    }

    @Test
    fun `game screen has search button disabled when in game`() = runTest {
        gameRepository.updateCurrentGame(CurrentGame("42", "No", "MaYbE"))
        composeTestRule.apply {
            onNodeWithTag("global-search-button")
                .assertIsDisplayed()
                .assertIsNotEnabled()
        }
    }

    @Test
    fun `team screen has search button enabled`() = runTest {
        gameRepository.updateCurrentGame(CurrentGame("42", "No", "MaYbE"))
        composeTestRule.apply {

            onNodeWithTag("${AnankeDestination.TEAM}-navigation-item")
                .performClick()

            onNodeWithTag("global-search-button")
                .assertIsDisplayed()
                .assertIsEnabled()
        }
    }

    @Test
    fun `you screen has search button disabled`() = runTest {
        gameRepository.updateCurrentGame(CurrentGame("42", "No", "MaYbE"))
        composeTestRule.apply {
            onNodeWithTag("${AnankeDestination.YOU}-navigation-item")
                .performClick()

            onNodeWithTag("global-search-button")
                .assertIsDisplayed()
                .assertIsNotEnabled()
        }
    }

    @Test
    fun `new game screen has search button disabled`() = runTest {
        composeTestRule.apply {
            onNodeWithTag("${GameDestination.HOME}-to-${GameDestination.NEW}-button", useUnmergedTree = true)
                .performClick()

            onNodeWithTag("global-search-button")
                .assertIsDisplayed()
                .assertIsNotEnabled()
        }
    }

    @Test
    fun `search dialog appears when search button is clicked`() = runTest {
        composeTestRule.apply {
            onNodeWithTag("global-search-button")
                .performClick()

            onNodeWithTag("global-search-dialogue")
                .assertIsDisplayed()
        }
    }

    @Test
    fun `search dialogue allows text input and confirmation dismisses dialogue`() = runTest {
        composeTestRule.apply {
            onNodeWithTag("global-search-button")
                .performClick()

            val inputText = "Test Search Query"

            onNodeWithTag("global-search-text-field")
                .performTextInput(inputText)

            onNodeWithTag("global-search-text-field")
                .assertTextContains(inputText)

            onNodeWithTag("global-search-bar-confirm-button")
                .performClick()

            onNodeWithTag("global-search-dialogue")
                .assertDoesNotExist()
        }
    }

    @Test
    fun `search dialogue can be dismissed with close button`() = runTest {
        composeTestRule.apply {
            onNodeWithTag("global-search-button")
                .performClick()

            onNodeWithTag("global-search-dialogue")
                .assertIsDisplayed()

            onNodeWithTag("global-search-bar-close-button")
                .performClick()

            onNodeWithTag("global-search-dialogue")
                .assertDoesNotExist()
        }
    }

    @Test
    fun `search causes games to be filtered`() = runTest {
        val controllableGameRepository = gameRepository as ControllableGameRepository
        controllableGameRepository.emitGames(
            listOf(
                Game("1", "Chess", "Strategy", creatorId = "1", DEFAULT_TEAM_SIZE),
                Game("2", "Monopoly", "Family", creatorId = "1", DEFAULT_TEAM_SIZE),
            )
        )

        composeTestRule.apply {
            onNodeWithTag("global-search-button")
                .performClick()

            val searchText = "Chess"

            onNodeWithTag("global-search-text-field")
                .performTextInput(searchText)

            onNodeWithTag("global-search-bar-confirm-button")
                .performClick()

            onNodeWithText("Chess")
                .assertIsDisplayed()
            onNodeWithText("Monopoly")
                .assertDoesNotExist()
        }
    }

    @Test
    fun `moving between screens causes filter to be reset`() = runTest {
        val controllableGameRepository = gameRepository as ControllableGameRepository
        controllableGameRepository.emitGames(
            listOf(
                Game("1", "Chess", "Strategy", creatorId = "1", DEFAULT_TEAM_SIZE),
                Game("2", "Monopoly", "Family", creatorId = "1", DEFAULT_TEAM_SIZE),
            )
        )

        composeTestRule.apply {
            onNodeWithTag("global-search-button")
                .performClick()

            val searchText = "Chess"

            onNodeWithTag("global-search-text-field")
                .performTextInput(searchText)

            onNodeWithTag("global-search-bar-confirm-button")
                .performClick()

            onNodeWithText("Chess")
                .assertIsDisplayed()
            onNodeWithText("Monopoly")
                .assertDoesNotExist()

            // Move to Team screen and back to reset filter.
            onNodeWithTag("${AnankeDestination.TEAM}-navigation-item")
                .performClick()

            onNodeWithTag("${AnankeDestination.GAME}-navigation-item")
                .performClick()

            // All games should be visible again.
            onNodeWithText("Chess")
                .assertIsDisplayed()
            onNodeWithText("Monopoly")
                .assertIsDisplayed()
        }
    }
}
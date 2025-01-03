package com.carkzis.ananke.navigation

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.carkzis.ananke.MainActivity
import com.carkzis.ananke.data.model.CurrentGame
import com.carkzis.ananke.data.repository.GameRepository
import com.carkzis.ananke.data.repository.TeamRepository
import com.carkzis.ananke.data.repository.YouRepository
import com.carkzis.ananke.di.DataModule
import com.carkzis.ananke.testdoubles.ControllableGameRepository
import com.carkzis.ananke.testdoubles.ControllableTeamRepository
import com.carkzis.ananke.testdoubles.ControllableYouRepository
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@HiltAndroidTest
@RunWith(RobolectricTestRunner::class)
@Config(application = HiltTestApplication::class)
@UninstallModules(DataModule::class)
class NavigationTest {

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
    fun `first screen is the game screen`() {
        composeTestRule.apply {
            assertScreenSelected(AnankeDestination.GAME)
        }
    }

    @Test
    fun `navigates to from game screen to team screen and you screens`() = runTest {
        gameRepository.updateCurrentGame(CurrentGame("42", "No", "MaYbE"))
        composeTestRule.apply {
            assertScreenSelected(AnankeDestination.GAME, inGame = true)

            onNodeWithTag("${AnankeDestination.TEAM}-navigation-item")
                .performClick()

            assertScreenSelected(AnankeDestination.TEAM, inGame = true)

            onNodeWithTag("${AnankeDestination.YOU}-navigation-item")
                .performClick()

            assertScreenSelected(AnankeDestination.YOU, inGame = true)
        }
    }

    @Test
    fun `screen prior to top level screens will always be games`() = runTest {
        gameRepository.updateCurrentGame(CurrentGame("42", "No", "MaYbE"))
        composeTestRule.apply {
            onNodeWithTag("${AnankeDestination.TEAM}-navigation-item")
                .performClick()
            onNodeWithTag("${AnankeDestination.YOU}-navigation-item")
                .performClick()

            activityRule.scenario.onActivity {
                it.onBackPressedDispatcher.onBackPressed()
            }

            assertNavigationItemSelected("${AnankeDestination.GAME}-navigation-item")
            onNodeWithTag("${GameDestination.HOME}-current-game-title")
                .assertIsDisplayed()
        }
    }

    @Test
    fun `cannot navigate to team or you screen if not in a game`() = runTest {
        composeTestRule.apply {
            onNodeWithTag("${AnankeDestination.TEAM}-navigation-item")
                .performClick()

            assertNavigationItemSelected("${AnankeDestination.GAME}-navigation-item")
            onNodeWithTag("${GameDestination.HOME}-title")
                .assertIsDisplayed()

            onNodeWithTag("${AnankeDestination.YOU}-navigation-item")
                .performClick()

            assertNavigationItemSelected("${AnankeDestination.GAME}-navigation-item")
            onNodeWithTag("${GameDestination.HOME}-title")
                .assertIsDisplayed()
        }
    }

    private fun AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>.assertScreenSelected(destination: AnankeDestination, inGame: Boolean = false) {
        val expectedTitle = when {
            destination == AnankeDestination.GAME && inGame -> "${GameDestination.HOME}-current-game-title"
            destination == AnankeDestination.GAME && !inGame -> "${GameDestination.HOME}-title"
            else -> "$destination-title"
        }
        val expectedDestination = "$destination-navigation-item"
        val expectedUnselectedNavigationItems = AnankeDestination.values().filter {
            it != destination
        }

        assertNavigationItemSelected(expectedDestination)

        waitUntil {
            composeTestRule
                .onAllNodesWithTag(expectedTitle)
                .fetchSemanticsNodes().size == 1
        }

        expectedUnselectedNavigationItems.forEach {
            assertNavigationItemNotSelected("$it-navigation-item")
        }
    }

    private fun AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>.assertNavigationItemSelected(tag: String) {
        onNodeWithTag(tag)
            .assertIsDisplayed()
            .assertIsSelected()
    }

    private fun AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>.assertNavigationItemNotSelected(tag: String) {
        onNodeWithTag(tag)
            .assertIsDisplayed()
            .assertIsNotSelected()
    }
}
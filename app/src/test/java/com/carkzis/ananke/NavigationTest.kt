package com.carkzis.ananke

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.carkzis.ananke.navigation.AnankeDestination
import com.carkzis.ananke.navigation.GameDestination
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
class NavigationTest {

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
    fun `navigates to from game screen to team screen and you screens`() {
        composeTestRule.apply {
            assertScreenSelected(AnankeDestination.GAME)

            onNodeWithTag("${AnankeDestination.TEAM}-navigation-item")
                .performClick()

            assertScreenSelected(AnankeDestination.TEAM)

            onNodeWithTag("${AnankeDestination.YOU}-navigation-item")
                .performClick()

            assertScreenSelected(AnankeDestination.YOU)
        }
    }

    @Test
    fun `navigates from game screen to the new game screen with expected navigation states`() {
        composeTestRule.apply {
            onNodeWithTag("${GameDestination.HOME}-to-${GameDestination.NEW}-button", useUnmergedTree = true)
                .assertIsDisplayed()
                .performClick()

            assertNavigationItemSelected("${AnankeDestination.GAME}-navigation-item")
            onNodeWithTag("${GameDestination.NEW}-title")
                .assertIsDisplayed()

            assertNavigationItemNotSelected("${AnankeDestination.TEAM}-navigation-item")
            assertNavigationItemNotSelected("${AnankeDestination.YOU}-navigation-item")
        }
    }

    @Test
    fun `navigates from new game screen back to game screen when back selected with expected navigation states`() {
        composeTestRule.apply {
            onNodeWithTag("${GameDestination.HOME}-to-${GameDestination.NEW}-button", useUnmergedTree = true)
                .performClick()

            onNodeWithTag("${GameDestination.NEW}-title")
                .assertIsDisplayed()

            activityRule.scenario.onActivity {
                it.onBackPressedDispatcher.onBackPressed()
            }

            assertNavigationItemSelected("${AnankeDestination.GAME}-navigation-item")
            onNodeWithTag("${GameDestination.HOME}-title")
                .assertIsDisplayed()
        }
    }

    @Test
    fun `navigates from new game screen back to game screen when new game added`() {
        composeTestRule.apply {
            onNodeWithTag("${GameDestination.HOME}-to-${GameDestination.NEW}-button", useUnmergedTree = true)
                .performClick()

            onNodeWithTag("${GameDestination.NEW}-addnewgame-button", useUnmergedTree = true)
                .performClick()

            assertScreenSelected(AnankeDestination.GAME)
        }
    }

    @Test
    fun `screen prior to top level screens will always be games`() {
        composeTestRule.apply {
            onNodeWithTag("${AnankeDestination.TEAM}-navigation-item")
                .performClick()
            onNodeWithTag("${AnankeDestination.YOU}-navigation-item")
                .performClick()

            activityRule.scenario.onActivity {
                it.onBackPressedDispatcher.onBackPressed()
            }

            assertNavigationItemSelected("${AnankeDestination.GAME}-navigation-item")
            onNodeWithTag("${GameDestination.HOME}-title")
                .assertIsDisplayed()
        }
    }

    private fun AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>.assertScreenSelected(destination: AnankeDestination) {
        val expectedTitle = if (destination == AnankeDestination.GAME) "${GameDestination.HOME}-title" else "$destination-title"
        val expectedDestination = "$destination-navigation-item"
        val expectedUnselectedNavigationItems = AnankeDestination.values().filter {
            it != destination
        }

        assertNavigationItemSelected(expectedDestination)
        onNodeWithTag(expectedTitle)
            .assertIsDisplayed()

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
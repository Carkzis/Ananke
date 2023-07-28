package com.example.ananke

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.example.ananke.navigation.AnankeDestination
import com.example.ananke.navigation.GameDestination
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
            assertNavigationItemSelected("${AnankeDestination.GAME}-navigation-item")
            onNodeWithTag("${GameDestination.HOME}-title")
                .assertIsDisplayed()

            assertNavigationItemNotSelected("${AnankeDestination.TEAM}-navigation-item")
            assertNavigationItemNotSelected("${AnankeDestination.YOU}-navigation-item")
        }
    }

    @Test
    fun `navigates from game screen to the new game screen with expected navigation states`() {
        composeTestRule.apply {
            onNodeWithTag(testTag = "${GameDestination.HOME}-to-${GameDestination.NEW}-button", useUnmergedTree = true)
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
            onNodeWithTag(testTag = "${GameDestination.HOME}-to-${GameDestination.NEW}-button", useUnmergedTree = true)
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
    fun `screen prior to top level screens will always be games`() {
        composeTestRule.apply {
            onNodeWithTag(testTag = "${AnankeDestination.TEAM}-navigation-item")
                .performClick()
            onNodeWithTag(testTag = "${AnankeDestination.YOU}-navigation-item")
                .performClick()

            activityRule.scenario.onActivity {
                it.onBackPressedDispatcher.onBackPressed()
            }

            assertNavigationItemSelected("${AnankeDestination.GAME}-navigation-item")
            onNodeWithTag("${GameDestination.HOME}-title")
                .assertIsDisplayed()
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
package com.carkzis.ananke

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeUp
import androidx.test.espresso.Espresso
import androidx.test.espresso.NoActivityResumedException
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.carkzis.ananke.navigation.AnankeDestination
import com.carkzis.ananke.navigation.GameDestination
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class NavigationTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 2)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test(expected = NoActivityResumedException::class)
    fun pressing_back_from_home_screen_closes_app() {
        composeTestRule.apply {
            Espresso.pressBack()
        }
    }

    @Test
    fun navigates_from_new_game_screen_back_to_game_screen_when_back_selected_with_expected_navigation_states() {
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
    fun navigates_from_game_screen_to_the_new_game_screen_with_expected_navigation_states() {
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
    fun navigates_from_new_game_screen_back_to_game_screen_when_new_game_added() {
        composeTestRule.apply {
            onNodeWithTag("${GameDestination.HOME}-to-${GameDestination.NEW}-button", useUnmergedTree = true)
                .performClick()

            onNodeWithTag("${GameDestination.NEW}-addnewgame-lazycolumn")
                .performTouchInput {
                    swipeUp()
                }

            onNodeWithTag("${GameDestination.NEW}-addnewgame-button-dummy", useUnmergedTree = true)
                .performClick()

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
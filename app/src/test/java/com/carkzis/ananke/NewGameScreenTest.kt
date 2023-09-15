package com.carkzis.ananke

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.carkzis.ananke.navigation.AnankeDestination
import com.carkzis.ananke.navigation.GameDestination
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import org.junit.Before
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
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setUp() {
        composeTestRule.apply {
            onNodeWithTag(
                "${GameDestination.HOME}-to-${GameDestination.NEW}-button",
                useUnmergedTree = true
            ).performClick()
        }
    }

    @Test
    fun `text box for new game title takes in typed characters`() {
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
        composeTestRule.apply {
            onNodeWithTag("${GameDestination.NEW}-game-description")
                .performClick()
                .performTextInput("A Game Description")
            onNodeWithTag("${GameDestination.NEW}-game-description")
                .assertTextContains("A Game Description")
        }
    }

    @Test
    fun `add new game results in it being added to database`() {
        composeTestRule.apply {
            onNodeWithTag("${GameDestination.NEW}-game-title")
                .performClick()
                .performTextInput("A Game Title")
            onNodeWithTag("${GameDestination.NEW}-game-description")
                .performClick()
                .performTextInput("A Game Description")
            onNodeWithTag("${GameDestination.NEW}-addnewgame-button", useUnmergedTree = true)
                .performClick()

            assertScreenSelected(AnankeDestination.GAME)

            // TODO: Need to confirm new game added to screen.
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
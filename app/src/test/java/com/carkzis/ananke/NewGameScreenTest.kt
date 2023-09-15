package com.carkzis.ananke

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.carkzis.ananke.navigation.GameDestination
import com.carkzis.ananke.testdoubles.DummyNewGameViewModel
import com.carkzis.ananke.ui.screens.NewGameScreen
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
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Before
    fun setUp() {
        composeTestRule.apply {
            composeTestRule.setContent {
                NewGameScreen(onAddGameClick = {}, viewModel = DummyNewGameViewModel())
            }
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
    fun `add new game results in redirect to game screen`() {
        composeTestRule.apply {
            onNodeWithTag("${GameDestination.NEW}-game-title")
                .performClick()
                .performTextInput("A Game Title")
            onNodeWithTag("${GameDestination.NEW}-game-description")
                .performClick()
                .performTextInput("A Game Description")
            onNodeWithTag("${GameDestination.NEW}-addnewgame-button", useUnmergedTree = true)
                .performClick()
        }
    }

}
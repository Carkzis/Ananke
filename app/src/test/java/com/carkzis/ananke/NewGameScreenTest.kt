package com.carkzis.ananke

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
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
            )
                .performClick()
        }
    }

    @Test
    fun `text box for new game title takes in typed characters`() {
        composeTestRule.apply {
            onNodeWithTag("${GameDestination.NEW}-game-title")
                .performClick()
        }
    }

}
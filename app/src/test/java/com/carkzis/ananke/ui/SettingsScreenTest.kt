package com.carkzis.ananke.ui

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import com.carkzis.ananke.data.model.User
import com.carkzis.ananke.navigation.AnankeDestination
import com.carkzis.ananke.testdoubles.ControllableYouRepository
import com.carkzis.ananke.ui.screens.settings.SettingsRoute
import com.carkzis.ananke.ui.screens.settings.SettingsScreen
import com.carkzis.ananke.ui.screens.settings.SettingsViewModel
import com.carkzis.ananke.utils.CurrentUserUseCase
import com.carkzis.ananke.utils.UpdateUsernameUseCase
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@HiltAndroidTest
@RunWith(RobolectricTestRunner::class)
@Config(application = HiltTestApplication::class)
class SettingsScreenTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun `settings screen elements are displayed`() {
        val currentUser = User(
            id = 1,
            name = "Test User"
        )

        composeTestRule.setContent {
            SettingsScreen(
                currentUser = currentUser
            )
        }

        composeTestRule.apply {
            onNodeWithTag("${AnankeDestination.SETTINGS}-title")
                .assertExists()

            onNodeWithTag("${AnankeDestination.SETTINGS}-current-user")
                .assertTextEquals(currentUser.name)

            onNodeWithTag("${AnankeDestination.SETTINGS}-change-username-button")
                .assertExists()
        }
    }

    @Test
    fun `settings screen displays dialogue when button clicked`() {
        val currentUser = User(
            id = 1,
            name = "Test User"
        )

        composeTestRule.setContent {
            SettingsScreen(
                currentUser = currentUser
            )
        }

        composeTestRule.apply {
            onNodeWithTag("${AnankeDestination.SETTINGS}-change-username-button")
                .performClick()

            onNodeWithTag("${AnankeDestination.SETTINGS}-change-username-dialog")
                .assertExists()
        }
    }

    @Test
    fun `settings screen dialogue dismisses when dismiss button clicked`() {
        val currentUser = User(
            id = 1,
            name = "Test User"
        )

        composeTestRule.setContent {
            SettingsScreen(
                currentUser = currentUser
            )
        }

        composeTestRule.apply {
            onNodeWithTag("${AnankeDestination.SETTINGS}-change-username-button")
                .performClick()

            onNodeWithTag("${AnankeDestination.SETTINGS}-change-username-dialog")
                .assertExists()

            onNodeWithTag("${AnankeDestination.SETTINGS}-change-username-dismiss-button")
                .performClick()

            onNodeWithTag("${AnankeDestination.SETTINGS}-change-username-dialog")
                .assertDoesNotExist()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `settings screen dialogue changes username when confirm button clicked`() = runTest {
        val newName = "Dave"
        val currentUser = User(
            id = 1,
            name = "Test User"
        )

        val youRepository = ControllableYouRepository()

        composeTestRule.setContent {
            SettingsRoute(
                viewModel = SettingsViewModel(
                    currentUserUseCase = CurrentUserUseCase(
                        youRepository = youRepository
                    ),
                    updateUsernameUseCase = UpdateUsernameUseCase(
                        youRepository = youRepository
                    )
                )
            )
        }

        youRepository.currentUser = currentUser

        composeTestRule.apply {
            onNodeWithTag("${AnankeDestination.SETTINGS}-change-username-button")
                .performClick()

            onNodeWithTag("${AnankeDestination.SETTINGS}-change-username-textfield")
                .performTextClearance()

            onNodeWithTag("${AnankeDestination.SETTINGS}-change-username-textfield")
                .performTextInput(newName)

            onNodeWithTag("${AnankeDestination.SETTINGS}-change-username-confirm-button")
                .performClick()

            onNodeWithTag("${AnankeDestination.SETTINGS}-change-username-dialog")
                .assertDoesNotExist()

            advanceUntilIdle()

            onNodeWithTag("${AnankeDestination.SETTINGS}-current-user")
                .assertTextEquals(newName)
        }
    }
}
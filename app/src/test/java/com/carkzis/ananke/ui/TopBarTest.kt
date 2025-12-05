package com.carkzis.ananke.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.carkzis.ananke.MainActivity
import com.carkzis.ananke.data.model.CurrentGame
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
}
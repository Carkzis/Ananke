package com.carkzis.ananke

import androidx.activity.ComponentActivity
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.carkzis.ananke.navigation.GameDestination
import com.carkzis.ananke.testdoubles.DummyGameRepository
import com.carkzis.ananke.ui.screens.GameRoute
import com.carkzis.ananke.ui.screens.GameScreenViewModel
import com.carkzis.ananke.ui.screens.nugame.NewGameRoute
import com.carkzis.ananke.ui.screens.nugame.NewGameValidatorFailure
import com.carkzis.ananke.ui.screens.nugame.NewGameViewModel
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@HiltAndroidTest
@RunWith(RobolectricTestRunner::class)
@Config(application = HiltTestApplication::class)
class GameScreenTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Before
    fun setUp() {
        composeTestRule.apply {
            composeTestRule.setContent {
                val gameRepository = DummyGameRepository()
                val gameStateUseCase = GameStateUseCase(gameRepository)
                GameRoute(
                    viewModel = GameScreenViewModel(gameStateUseCase, gameRepository)
                )
            }
        }
    }

    @Test
    fun `no title when loading`() {
        composeTestRule.apply {
            onNodeWithTag("${GameDestination.HOME}-title")
                .assertDoesNotExist()
        }
    }

    @Test
    fun `expected list of games displays with expected details when outside game`() {

    }

    @Test
    fun `enter expected game via dialog`() {

    }

    @Test
    fun `exit a game so that a list of games displays again`() {

    }

    @Test
    fun `dismiss an enter game dialog to remove it`() {

    }

}
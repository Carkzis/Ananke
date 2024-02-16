package com.carkzis.ananke

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.carkzis.ananke.testdoubles.DummyGameRepository
import com.carkzis.ananke.ui.screens.GameRoute
import com.carkzis.ananke.ui.screens.GameScreenViewModel
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import org.junit.Before
import org.junit.Rule
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

}
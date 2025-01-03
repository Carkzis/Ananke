package com.carkzis.ananke.ui

import androidx.test.core.app.ActivityScenario.launch
import com.carkzis.ananke.data.model.CurrentGame
import com.carkzis.ananke.data.network.toDomainUser
import com.carkzis.ananke.data.network.userForTesting
import com.carkzis.ananke.testdoubles.ControllableGameRepository
import com.carkzis.ananke.testdoubles.ControllableYouDao
import com.carkzis.ananke.testdoubles.ControllableYouRepository
import com.carkzis.ananke.ui.screens.you.YouViewModel
import com.carkzis.ananke.utils.GameStateUseCase
import com.carkzis.ananke.utils.MainDispatcherRule
import com.carkzis.ananke.utils.assertNameHasExpectedFormat
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class YouViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: YouViewModel
    private lateinit var gameRepository: ControllableGameRepository
    private lateinit var youRepository: ControllableYouRepository
    private lateinit var youDao: ControllableYouDao

    @Before
    fun setUp() {
        gameRepository = ControllableGameRepository()
        youRepository = ControllableYouRepository()
        viewModel = YouViewModel(GameStateUseCase(gameRepository), youRepository)
    }

    @Test
    fun `view model displays no game title if unavailable`() = runTest {
        val expectedGameTitle = ""

        var actualGameTitle = "NOT EMPTY"
        val collection = launch(UnconfinedTestDispatcher()) {
            viewModel.currentGame.collect {
                actualGameTitle = it.name
            }
        }

        Assert.assertEquals(expectedGameTitle, actualGameTitle)

        collection.cancel()
    }

    @Test
    fun `view model displays game title if available`() = runTest {
        val expectedGameTitle = "A Game"
        val currentGame = CurrentGame("1", expectedGameTitle, "A Description")

        var actualGameTitle = ""
        val collection = launch(UnconfinedTestDispatcher()) {
            viewModel.currentGame.collect {
                actualGameTitle = it.name
            }
        }

        gameRepository.emitCurrentGame(currentGame)

        assertEquals(expectedGameTitle, actualGameTitle)

        collection.cancel()
    }

    @Test
    fun `view model adds current user when initialised`() = runTest {
        val expectedUserId = userForTesting.id
        val currentGame = CurrentGame("1", "A Game", "A Description")
        youRepository.currentUserId = expectedUserId

        gameRepository.emitCurrentGame(currentGame)

        var actualUserId = ""
        val collection = launch(UnconfinedTestDispatcher()) {
            viewModel.currentGame.collect {
                actualUserId = youRepository.getCharacterForUser(userForTesting.toDomainUser(), it.id.toLong()).first().id
            }
        }

        assertEquals(expectedUserId, actualUserId.toLong())

        collection.cancel()
    }

    @Test
    fun `view model displays an initial character name`() = runTest {
        val currentGame = CurrentGame("1", "A Game", "A Description")
        youRepository.currentUserId = userForTesting.id

        gameRepository.emitCurrentGame(currentGame)

        var actualCharacterName = ""
        val collection = launch(UnconfinedTestDispatcher()) {
            viewModel.character.collect {
                actualCharacterName = it.character
            }
        }

        assertNameHasExpectedFormat(actualCharacterName)

        collection.cancel()
    }

    @Test
    fun `view model displays an empty initial biography`() = runTest {
        val currentGame = CurrentGame("1", "A Game", "A Description")
        youRepository.currentUserId = userForTesting.id

        gameRepository.emitCurrentGame(currentGame)

        var actualCharacterBio = ""
        val collection = launch(UnconfinedTestDispatcher()) {
            viewModel.character.collect {
                actualCharacterBio = it.bio
            }
        }

        assertTrue(actualCharacterBio.isEmpty())

        collection.cancel()
    }

    @Test
    fun `view model changes character name`() = runTest {
        val expectedCharacterName = "A New Name"
        val currentGame = CurrentGame("1", "A Game", "A Description")

        gameRepository.emitCurrentGame(currentGame)

        var actualCharacterName = ""
        val collection = launch(UnconfinedTestDispatcher()) {
            viewModel.character.collect {
                actualCharacterName = it.character
            }
        }

        viewModel.changeCharacterName(expectedCharacterName)

        assertEquals(expectedCharacterName, actualCharacterName)

        collection.cancel()
    }

    @Test
    fun `view model changes character bio`() = runTest {
        val expectedCharacterBio = "A bio for the character."
        val currentGame = CurrentGame("1", "A Game", "A Description")

        gameRepository.emitCurrentGame(currentGame)

        var actualCharacterBio = ""
        val collection = launch(UnconfinedTestDispatcher()) {
            viewModel.character.collect {
                actualCharacterBio = it.bio
            }
        }

        viewModel.changeCharacterBio(expectedCharacterBio)

        assertEquals(expectedCharacterBio, actualCharacterBio)

        collection.cancel()
    }

    @Test
    fun `view model initially displays character name when editing`() = runTest {
        collectInitialCharacterInformation()

        var editableCharacterName = ""
        val collection = launch(UnconfinedTestDispatcher()) {
            viewModel.editableCharacter.collect {
                editableCharacterName = it
            }
        }

        viewModel.beginEditingCharacterName()

        assertNameHasExpectedFormat(editableCharacterName)

        collection.cancel()
    }

    private fun TestScope.collectInitialCharacterInformation() {
        val currentGame = CurrentGame("1", "A Game", "A Description")
        youRepository.currentUserId = userForTesting.id
        gameRepository.emitCurrentGame(currentGame)

        val collection = launch(UnconfinedTestDispatcher()) {
            viewModel.character.collect {}
        }
        
        collection.cancel()
    }

}
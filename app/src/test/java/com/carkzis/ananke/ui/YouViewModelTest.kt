package com.carkzis.ananke.ui

import com.carkzis.ananke.data.model.CurrentGame
import com.carkzis.ananke.data.network.toDomainUser
import com.carkzis.ananke.data.network.userForTesting
import com.carkzis.ananke.testdoubles.ControllableGameRepository
import com.carkzis.ananke.testdoubles.ControllableYouRepository
import com.carkzis.ananke.ui.screens.you.CharacterNotInEditModeException
import com.carkzis.ananke.ui.screens.you.EditMode
import com.carkzis.ananke.ui.screens.you.YouConstants
import com.carkzis.ananke.ui.screens.you.YouValidatorFailure
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
            viewModel.editableCharacterName.collect {
                editableCharacterName = it
            }
        }

        viewModel.beginEditingCharacterName()

        assertNameHasExpectedFormat(editableCharacterName)

        collection.cancel()
    }

    @Test
    fun `view model edits displayed character name`() = runTest {
        collectInitialCharacterInformation()

        val expectedCharacterName = "A New Name"
        var editableCharacterName = ""
        val collection = launch(UnconfinedTestDispatcher()) {
            viewModel.editableCharacterName.collect {
                editableCharacterName = it
            }
        }

        viewModel.beginEditingCharacterName()
        viewModel.editCharacterName(expectedCharacterName)

        assertEquals(expectedCharacterName, editableCharacterName)

        collection.cancel()
    }

    @Test
    fun `view model does not send toast when edited name is empty but not confirmed`() = runTest {
        val expectedCharacterName = ""

        collectInitialCharacterInformation()

        var message = ""
        val collection = launch(UnconfinedTestDispatcher()) {
            viewModel.message.collect { message = it }
        }

        viewModel.beginEditingCharacterName()
        viewModel.editCharacterName(expectedCharacterName)

        Assert.assertEquals("", message)

        collection.cancel()
    }

    @Test
    fun `view model sends toast when edited name confirmed and is shorter than 3 characters`() = runTest {
        val newNameLength = YouConstants.MINIMUM_CHARACTER_NAME_LENGTH - 1
        val expectedCharacterName = "A".repeat(newNameLength)

        collectInitialCharacterInformation()

        var message = ""
        val collection = launch(UnconfinedTestDispatcher()) {
            viewModel.message.collect { message = it }
        }

        viewModel.beginEditingCharacterName()
        viewModel.editCharacterName(expectedCharacterName)

        viewModel.changeCharacterName(expectedCharacterName)

        Assert.assertEquals(YouValidatorFailure.NAME_TOO_SHORT.message, message)

        collection.cancel()
    }

    @Test
    fun `view model sends toast when edited name longer than 30 characters`() = runTest {
        val newNameLength = YouConstants.MAXIMUM_CHARACTER_NAME_LENGTH + 1
        val expectedCharacterName = "A".repeat(newNameLength)

        collectInitialCharacterInformation()

        var message = ""
        val collection = launch(UnconfinedTestDispatcher()) {
            viewModel.message.collect { message = it }
        }

        viewModel.beginEditingCharacterName()
        viewModel.editCharacterName(expectedCharacterName)

        Assert.assertEquals(YouValidatorFailure.NAME_TOO_LONG.message, message)

        collection.cancel()
    }

    @Test
    fun `view model exits edit mode when changing character name`() = runTest {
        val firstNewName = "David"

        collectInitialCharacterInformation()

        viewModel.beginEditingCharacterName()
        viewModel.editCharacterName(firstNewName)
        viewModel.changeCharacterName(firstNewName)

        var editMode: EditMode = EditMode.CharacterName
        val collection = launch(UnconfinedTestDispatcher()) {
            viewModel.editMode.collect { editMode = it }
        }

        assertEquals(EditMode.None, editMode)

        collection.cancel()
    }

    @Test
    fun `view model initially displays character bio when editing`() = runTest {
        val expectedCharacterBio = "A bio for the character."
        collectInitialCharacterInformation(expectedCharacterBio = expectedCharacterBio)

        var editableBio = ""
        val collection = launch(UnconfinedTestDispatcher()) {
            viewModel.editableCharacterBio.collect {
                editableBio = it
            }
        }

        viewModel.beginEditingCharacterBio()

        assertEquals(expectedCharacterBio, editableBio)

        collection.cancel()
    }

    @Test
    fun `view model edits displayed character bio`() = runTest {
        val originalCharacterBio = "A bio for the character."
        val expectedCharacterBio = "A new bio for the character."
        collectInitialCharacterInformation(expectedCharacterBio = originalCharacterBio)

        var editableBio = ""
        val collection = launch(UnconfinedTestDispatcher()) {
            viewModel.editableCharacterBio.collect {
                editableBio = it
            }
        }

        viewModel.beginEditingCharacterBio()
        viewModel.editCharacterBio(expectedCharacterBio)

        assertEquals(expectedCharacterBio, editableBio)

        collection.cancel()
    }

    @Test
    fun `view model sends toast when edited bio longer than 20 characters`() = runTest {
        val newNameLength = YouConstants.MAXIMUM_CHARACTER_NAME_LENGTH + 1
        val expectedCharacterName = "A".repeat(newNameLength)

        collectInitialCharacterInformation()

        var message = ""
        val collection = launch(UnconfinedTestDispatcher()) {
            viewModel.message.collect { message = it }
        }

        viewModel.beginEditingCharacterName()
        viewModel.editCharacterName(expectedCharacterName)

        Assert.assertEquals(YouValidatorFailure.NAME_TOO_LONG.message, message)

        collection.cancel()
    }

    @Test
    fun `view model exits edit mode when changing character bio`() = runTest {
        val firstNewBio = "A New Bio"
        val secondNewBio = "Another New Bio"

        collectInitialCharacterInformation()

        viewModel.beginEditingCharacterBio()
        viewModel.editCharacterBio(firstNewBio)
        viewModel.changeCharacterName(firstNewBio)

        var editMode: EditMode = EditMode.CharacterBio
        val collection = launch(UnconfinedTestDispatcher()) {
            viewModel.editMode.collect { editMode = it }
        }

        assertEquals(EditMode.None, editMode)

        collection.cancel()
    }

    @Test
    fun `view model sends when changing character if not in edit mode`() = runTest {
        val firstNewName = "David"

        collectInitialCharacterInformation()

        var message = ""
        val collection = launch(UnconfinedTestDispatcher()) {
            viewModel.message.collect { message = it }
        }

        viewModel.editCharacterName(firstNewName)

        Assert.assertEquals(CharacterNotInEditModeException().message, message)

        collection.cancel()
    }

    @Test
    fun `cancelling edit mode exits edit mode`() = runTest {
        collectInitialCharacterInformation()

        viewModel.beginEditingCharacterBio()
        viewModel.cancelEdit()

        var editMode: EditMode = EditMode.CharacterBio
        val collection = launch(UnconfinedTestDispatcher()) {
            viewModel.editMode.collect { editMode = it }
        }

        assertEquals(EditMode.None, editMode)

        collection.cancel()
    }

    private fun TestScope.collectInitialCharacterInformation(expectedCharacterBio: String = "") {
        val currentGame = CurrentGame("1", "A Game", "A Description")

        youRepository.currentUserId = userForTesting.id
        gameRepository.emitCurrentGame(currentGame)

        val collection = launch(UnconfinedTestDispatcher()) {
            viewModel.character.collect {}
        }

        viewModel.changeCharacterBio(expectedCharacterBio)

        collection.cancel()
    }

}
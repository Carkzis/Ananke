package com.carkzis.ananke.ui

import com.carkzis.ananke.data.database.toDomain
import com.carkzis.ananke.data.model.Game
import com.carkzis.ananke.data.model.User
import com.carkzis.ananke.data.model.toCurrentGame
import com.carkzis.ananke.testdoubles.ControllableGameRepository
import com.carkzis.ananke.testdoubles.ControllableTeamRepository
import com.carkzis.ananke.testdoubles.ControllableYouRepository
import com.carkzis.ananke.testdoubles.dummyUserEntities
import com.carkzis.ananke.ui.screens.game.CreatorIdDoesNotMatchException
import com.carkzis.ananke.ui.screens.game.EnterGameFailedException
import com.carkzis.ananke.ui.screens.game.ExitGameFailedException
import com.carkzis.ananke.ui.screens.game.GameDoesNotExistException
import com.carkzis.ananke.ui.screens.game.GameViewModel
import com.carkzis.ananke.ui.screens.game.GamingState
import com.carkzis.ananke.ui.screens.game.InvalidGameException
import com.carkzis.ananke.utils.CleanUpCharactersAndTeamMembersUseCase
import com.carkzis.ananke.utils.DeletableGameUseCase
import com.carkzis.ananke.utils.GameStateUseCase
import com.carkzis.ananke.utils.MainDispatcherRule
import com.carkzis.ananke.utils.OnboardUserUseCase
import com.carkzis.ananke.utils.PlayerCountUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class GameViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: GameViewModel
    private lateinit var gameRepository: ControllableGameRepository
    private lateinit var youRepository: ControllableYouRepository

    private lateinit var teamRepository: ControllableTeamRepository

    @Before
    fun setUp() {
        gameRepository = ControllableGameRepository()
        youRepository = ControllableYouRepository()
        teamRepository = ControllableTeamRepository()

        viewModel = GameViewModel(
            gameStateUseCase = GameStateUseCase(gameRepository),
            onboardUserUseCase = OnboardUserUseCase(youRepository),
            deletableGameUseCase = DeletableGameUseCase(youRepository),
            cleanUpCharactersAndTeamMembersUseCase = CleanUpCharactersAndTeamMembersUseCase(
                teamRepository = teamRepository,
                youRepository = youRepository
            ),
            gameRepository = gameRepository,
            playerCountUseCase = PlayerCountUseCase(teamRepository)
        )
    }

    @Test
    fun `view model displays expected list of games`() = runTest {
        val collection = launch(UnconfinedTestDispatcher()) {
            viewModel.gameList.collect {}
        }

        gameRepository.emitGames(dummyGames())
        assertEquals(dummyGames(), viewModel.gameList.value)

        collection.cancel()
    }

    @Test
    fun `view model provides loading state initially`() = runTest {
        val expectedCurrentGamingState = GamingState.Loading

        val actualCurrentGamingState = viewModel.gamingState.value
        assertEquals(expectedCurrentGamingState, actualCurrentGamingState)
    }

    @Test
    fun `view model provides out of game state when no current game`() = runTest {
        val expectedCurrentGamingState = GamingState.OutOfGame

        val collection = launch(UnconfinedTestDispatcher()) {
            viewModel.gamingState.collect()
        }

        val actualCurrentGamingState = viewModel.gamingState.value
        assertEquals(expectedCurrentGamingState, actualCurrentGamingState)

        collection.cancel()
    }

    @Test
    fun `view model updates state to in game with current game`() = runTest {
        val expectedCurrentGame = dummyGames().first().toCurrentGame()

        val collection = launch(UnconfinedTestDispatcher()) {
            viewModel.gamingState.collect()
        }

        viewModel.enterGame(expectedCurrentGame)

        val actualCurrentGame = (viewModel.gamingState.value as GamingState.InGame).currentGame
        assertEquals(expectedCurrentGame, actualCurrentGame)

        collection.cancel()
    }

    @Test
    fun `view model exits game reverting to state out of current game`() = runTest {
        val currentGame = dummyGames().first().toCurrentGame()
        val expectedCurrentGamingState = GamingState.OutOfGame
        viewModel.enterGame(currentGame)

        val collection = launch(UnconfinedTestDispatcher()) {
            viewModel.gamingState.collect()
        }

        viewModel.exitGame()

        val actualCurrentGamingState = viewModel.gamingState.value
        assertEquals(expectedCurrentGamingState, actualCurrentGamingState)

        collection.cancel()
    }

    @Test
    fun `view model deletes game successfully`() = runTest {
        val gameToDelete = dummyGames().first()

        val collection = launch(UnconfinedTestDispatcher()) {
            viewModel.gameList.collect {}
        }

        gameRepository.emitGames(dummyGames())

        viewModel.deleteGame(gameToDelete)

        val actualGameList = viewModel.gameList.value
        assertEquals(dummyGames().size - 1, actualGameList.size)
        assertEquals(false, actualGameList.contains(gameToDelete))

        collection.cancel()
    }

    @Test
    fun `view model provides list of deletable games`() = runTest {
        val currentUser = User(dummyGames().first().creatorId.toLong(), "User 1")
        youRepository.currentUser = currentUser
        val deletableGames = mutableListOf<Game>()
        val collection = launch(UnconfinedTestDispatcher()) {
            viewModel.deletableGames.collect {
                deletableGames.addAll(it)
            }
        }

        gameRepository.emitGames(dummyGames())

        val expectedSizeOfGames = dummyGames().filter { it.creatorId == currentUser.id.toString() }.size
        assertEquals(expectedSizeOfGames, deletableGames.size)

        collection.cancel()
    }

    @Test
    fun `view model sends toast message about when failing to enter game`() = runTest {
        val currentGame = dummyGames().first().toCurrentGame()
        val messages = mutableListOf<String>()

        val collection = launch(UnconfinedTestDispatcher()) {
            viewModel.message.collect { messages.add(it) }
        }

        gameRepository.ENTRY_GENERIC_FAIL = true
        viewModel.enterGame(currentGame)

        assertEquals(EnterGameFailedException().message, messages.firstOrNull())
        assertEquals(1, messages.size)

        collection.cancel()
    }

    @Test
    fun `view model sends toast message about when game is invalid`() = runTest {
        val currentGame = dummyGames().first().toCurrentGame()
        val messages = mutableListOf<String>()

        val collection = launch(UnconfinedTestDispatcher()) {
            viewModel.message.collect { messages.add(it) }
        }

        gameRepository.ENTRY_GAME_INVALID = true
        viewModel.enterGame(currentGame)

        assertEquals(InvalidGameException().message, messages.firstOrNull())
        assertEquals(1, messages.size)

        collection.cancel()
    }

    @Test
    fun `view model sends toast message about when game does not exist`() = runTest {
        val currentGame = dummyGames().first().toCurrentGame()
        val messages = mutableListOf<String>()

        val collection = launch(UnconfinedTestDispatcher()) {
            viewModel.message.collect { messages.add(it) }
        }

        gameRepository.ENTRY_GAME_EXISTS = false
        viewModel.enterGame(currentGame)

        assertEquals(GameDoesNotExistException().message, messages.firstOrNull())
        assertEquals(1, messages.size)

        collection.cancel()
    }

    @Test
    fun `view model sends toast message about when failing to exit game`() = runTest {
        val messages = mutableListOf<String>()

        val collection = launch(UnconfinedTestDispatcher()) {
            viewModel.message.collect { messages.add(it) }
        }

        gameRepository.FAIL_EXIT = true
        viewModel.exitGame()

        assertEquals(ExitGameFailedException().message, messages.firstOrNull())
        assertEquals(1, messages.size)

        collection.cancel()
    }

    @Test
    fun `view model sends toast message when failing to delete game with creator ID mismatch`() = runTest {
        val gameToDelete = dummyGames().first()
        val messages = mutableListOf<String>()

        val gameCollection = launch(UnconfinedTestDispatcher()) {
            viewModel.gameList.collect {}
        }

        val messageCollection = launch(UnconfinedTestDispatcher()) {
            viewModel.message.collect { messages.add(it) }
        }

        gameRepository.CREATOR_ID_MISMATCH = true
        gameRepository.emitGames(dummyGames())

        viewModel.deleteGame(gameToDelete)

        val actualGameList = viewModel.gameList.value
        assertEquals(dummyGames().size, actualGameList.size)
        assertEquals(true, actualGameList.contains(gameToDelete))

        assertEquals(CreatorIdDoesNotMatchException().message, messages.firstOrNull())
        assertEquals(1, messages.size)

        gameCollection.cancel()
        messageCollection.cancel()
    }

    @Test
    fun `view model sends toast message when failing to delete game as game does not exist`() = runTest {
        val gameToDelete = dummyGames().first()
        val messages = mutableListOf<String>()

        val gameCollection = launch(UnconfinedTestDispatcher()) {
            viewModel.gameList.collect {}
        }

        val messageCollection = launch(UnconfinedTestDispatcher()) {
            viewModel.message.collect { messages.add(it) }
        }

        gameRepository.DELETE_GAME_EXISTS = false
        gameRepository.emitGames(dummyGames())

        viewModel.deleteGame(gameToDelete)

        val actualGameList = viewModel.gameList.value
        assertEquals(dummyGames().size, actualGameList.size)
        assertEquals(true, actualGameList.contains(gameToDelete))

        assertEquals(GameDoesNotExistException().message, messages.firstOrNull())
        assertEquals(1, messages.size)

        gameCollection.cancel()
        messageCollection.cancel()
    }

    @Test
    fun `view model creates a new user for the device if unavailable`() = runTest {
        assertEquals(youRepository.currentUser, dummyUserEntities.first().toDomain())
    }

    @Test
    fun `view model cleans up team members and characters when deleting a game`() = runTest {
        val gameToDelete = dummyGames().first()

        val gameCollection = launch(UnconfinedTestDispatcher()) {
            viewModel.gameList.collect {}
        }

        gameRepository.emitGames(dummyGames())

        viewModel.deleteGame(gameToDelete)

        assertEquals(true, teamRepository.teamMembersDeletedCalled)
        assertEquals(true, youRepository.charactersDeletedCalled)

        gameCollection.cancel()
    }

    fun dummyGames() = listOf(
        Game("1", "My First Game", "It is the first one.", "1", 2),
        Game("2", "My Second Game", "It is the second one.", "1", 3),
        Game("3", "My Third Game", "It is the third one.", "1", 4),
        Game("4", "Someone else's game", "It belongs to someone else", "2", 5)
    )
}
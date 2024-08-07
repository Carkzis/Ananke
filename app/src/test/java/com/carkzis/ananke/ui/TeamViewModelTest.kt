package com.carkzis.ananke.ui

import com.carkzis.ananke.data.CurrentGame
import com.carkzis.ananke.data.Game
import com.carkzis.ananke.data.User
import com.carkzis.ananke.data.toDomain
import com.carkzis.ananke.testdoubles.ControllableGameRepository
import com.carkzis.ananke.testdoubles.ControllableTeamRepository
import com.carkzis.ananke.testdoubles.dummyUserEntities
import com.carkzis.ananke.ui.screens.team.TeamViewModel
import com.carkzis.ananke.ui.screens.team.TooManyUsersInTeamException
import com.carkzis.ananke.ui.screens.team.UserAddedToNonExistentGameException
import com.carkzis.ananke.ui.screens.team.UserAlreadyExistsException
import com.carkzis.ananke.utils.GameStateUseCase
import com.carkzis.ananke.utils.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class TeamViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: TeamViewModel
    private lateinit var gameRepository: ControllableGameRepository
    private lateinit var teamRepository: ControllableTeamRepository

    @Before
    fun setUp() {
        gameRepository = ControllableGameRepository()
        teamRepository = ControllableTeamRepository()
        viewModel = TeamViewModel(GameStateUseCase(gameRepository), gameRepository, teamRepository)
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

        assertEquals(expectedGameTitle, actualGameTitle)

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
    fun `view model adds new team mate to game`() = runTest {
        val expectedTeamMember = User(1, "Zidun")
        val expectedGame = Game("1", "A Game", "A Description")
        gameRepository.emitGames(listOf(expectedGame))

        val users = mutableListOf<User>()
        val collection = launch(UnconfinedTestDispatcher()) {
            teamRepository.getUsers().collect { users.add(it.last()) }
        }

        viewModel.addTeamMember(expectedTeamMember, expectedGame)

        assertTrue(users.contains(expectedTeamMember))

        collection.cancel()
    }

    @Test
    fun `view model gets current list of available users`() = runTest {
        val collection = launch(UnconfinedTestDispatcher()) {
            viewModel.potentialTeamMemberList.collect {}
        }

        val expectedAvailableUsers = dummyUserEntities.map { it.toDomain() }
        teamRepository.emitUsers(expectedAvailableUsers)

        assertEquals(expectedAvailableUsers, viewModel.potentialTeamMemberList.value)

        collection.cancel()
    }

    @Test
    fun `view model gets users available for current game`() = runTest {
        val currentGame = CurrentGame("1", "A Game", "A Description")
        val allUsers = dummyUserEntities.map { it.toDomain() }
        val usersInGame = allUsers.dropLast(1)

        gameRepository.updateCurrentGame(currentGame)
        usersInGame.forEach {
            teamRepository.addTeamMember(it, currentGame.id.toLong())
        }

        val users = mutableListOf<User>()
        val collection = launch(UnconfinedTestDispatcher()) {
            viewModel.currentTeamMembers.collect {
                users.addAll(it)
            }
        }

        assertEquals(usersInGame, users)

        collection.cancel()
    }

    @Test
    fun `view model changes users available for current game when current game changes`() = runTest {
        val gameOne = CurrentGame("1", "A Game", "A Description")
        val gameTwo = CurrentGame("2", "A Different Game", "Another Description")
        val gameOneTeamMembers = listOf(
            User(id = 1, name = "Zidun"),
            User(id = 2, name = "Vivu"),
            User(id = 3, name = "Steinur"),
            User(id = 4, name = "Garnut")
        )
        val gameTwoTeamMembers = listOf(
            User(id = 6, name = "Freyu"),
            User(id = 7, name = "Quinu"),
            User(id = 8, name = "Eiku"),
        )

        gameOneTeamMembers.forEach {
            teamRepository.addTeamMember(it, gameOne.id.toLong())
        }
        gameTwoTeamMembers.forEach {
            teamRepository.addTeamMember(it, gameTwo.id.toLong())
        }
        gameRepository.updateCurrentGame(gameOne)

        val users = mutableListOf<List<User>>()
        val collection = launch(UnconfinedTestDispatcher()) {
            viewModel.currentTeamMembers.collect {
                users.add(it)
            }
        }

        gameRepository.updateCurrentGame(gameTwo)

        assertTrue(users.contains(gameOneTeamMembers))
        assertEquals(gameTwoTeamMembers, users.last())

        collection.cancel()
    }

    @Test
    fun `view model does not add users to non-existent game with message`() = runTest {
        val expectedTeamMember = User(1, "Zidun")
        val nonExistentGame = Game("999", "Non-Existent Game", "It does not exist.")

        var message = ""
        val collection = launch(UnconfinedTestDispatcher()) {
            viewModel.message.collect { message = it }
        }

        viewModel.addTeamMember(expectedTeamMember, nonExistentGame)
        gameRepository.emitGames(listOf(Game("1", "A Game", "A Description")))

        assertEquals(UserAddedToNonExistentGameException(nonExistentGame.name).message, message)

        collection.cancel()
    }

    @Test
    fun `view model sends message if attempt to add user to game when limit reached`() = runTest {
        val currentGame = CurrentGame("1", "A Game", "A Description")
        val expectedTeamMembers = listOf(
            User(id = 1, name = "Zidun"),
            User(id = 2, name = "Vivu"),
            User(id = 3, name = "Steinur"),
            User(id = 4, name = "Garnut")
        )
        val surplusTeamMember = User(id = 5, name = "Amarunt")
        gameRepository.updateCurrentGame(currentGame)
        teamRepository.limit = 4

        expectedTeamMembers.forEach {
            teamRepository.addTeamMember(it, currentGame.id.toLong())
        }

        var message = ""
        val collection = launch(UnconfinedTestDispatcher()) {
            viewModel.message.collect {
                message = it
            }
        }

        viewModel.addTeamMember(surplusTeamMember, currentGame.toGame())
        gameRepository.emitGames(listOf(currentGame.toGame()))

        assertEquals(TooManyUsersInTeamException(teamRepository.limit).message, message)

        collection.cancel()
    }

    @Test
    fun `view model sends message if attempt to add user to game user with id that already exists`() = runTest {
        val expectedTeamMember = User(1, "Zidun")
        val teamMemberWithIdenticalId = User(1, "Zudin")
        val expectedGame = Game("1", "A Game", "A Description")
        teamRepository.addTeamMember(expectedTeamMember, expectedGame.id.toLong())
        gameRepository.emitGames(listOf(expectedGame))

        var message = ""
        val collection = launch(UnconfinedTestDispatcher()) {
            viewModel.message.collect {
                message = it
            }
        }

        viewModel.addTeamMember(teamMemberWithIdenticalId, expectedGame)

        assertEquals(UserAlreadyExistsException().message, message)

        collection.cancel()
    }

    private fun CurrentGame.toGame() = Game(this.id, this.name, this.description)

}
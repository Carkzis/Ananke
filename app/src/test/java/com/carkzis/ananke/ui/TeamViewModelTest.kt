package com.carkzis.ananke.ui

import com.carkzis.ananke.data.CurrentGame
import com.carkzis.ananke.data.Game
import com.carkzis.ananke.data.User
import com.carkzis.ananke.testdoubles.ControllableGameRepository
import com.carkzis.ananke.testdoubles.ControllableTeamRepository
import com.carkzis.ananke.ui.screens.team.TeamViewModel
import com.carkzis.ananke.ui.screens.team.UserAddedToNonExistentGameException
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

        val users = mutableListOf<User>()
        val collection = launch(UnconfinedTestDispatcher()) {
            teamRepository.getUsers().collect { users.add(it.last()) }
        }

        viewModel.addTeamMember(expectedTeamMember, expectedGame)

        assertTrue(users.contains(expectedTeamMember))

        collection.cancel()
    }

    @Test
    fun `view model does not add users to non-existent game with exception`() = runTest {
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
    fun `view model gets current list of available users`() = runTest {

    }

    @Test
    fun `view model gets users available for current game`() = runTest {

    }

    @Test
    fun `other exceptions`() = runTest {

    }

}
package com.carkzis.ananke.ui

import com.carkzis.ananke.data.database.toDomain
import com.carkzis.ananke.data.model.CurrentGame
import com.carkzis.ananke.data.model.Game
import com.carkzis.ananke.data.model.GameCharacter
import com.carkzis.ananke.data.model.NewCharacter
import com.carkzis.ananke.data.model.User
import com.carkzis.ananke.testdoubles.ControllableGameRepository
import com.carkzis.ananke.testdoubles.ControllableTeamRepository
import com.carkzis.ananke.testdoubles.ControllableYouRepository
import com.carkzis.ananke.testdoubles.dummyUserEntities
import com.carkzis.ananke.ui.screens.team.TeamEvent
import com.carkzis.ananke.ui.screens.team.TeamViewModel
import com.carkzis.ananke.ui.screens.team.TooManyUsersInTeamException
import com.carkzis.ananke.ui.screens.team.UserAddedToNonExistentGameException
import com.carkzis.ananke.ui.screens.team.UserAlreadyExistsException
import com.carkzis.ananke.utils.AddCurrentUserToTheirEmptyGameUseCase
import com.carkzis.ananke.utils.AddTeamMemberUseCase
import com.carkzis.ananke.utils.CheckGameExistsUseCase
import com.carkzis.ananke.utils.GameStateUseCase
import com.carkzis.ananke.utils.MainDispatcherRule
import com.carkzis.ananke.utils.UserCharacterUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
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
    private lateinit var youRepository: ControllableYouRepository

    @Before
    fun setUp() {
        gameRepository = ControllableGameRepository()
        teamRepository = ControllableTeamRepository()
        youRepository = ControllableYouRepository()
        viewModel = TeamViewModel(
            GameStateUseCase(gameRepository),
            AddCurrentUserToTheirEmptyGameUseCase(teamRepository, youRepository),
            AddTeamMemberUseCase(teamRepository, youRepository),
            UserCharacterUseCase(youRepository),
            CheckGameExistsUseCase(gameRepository),
            teamRepository
        )
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
    fun `view model adds new team mate to game along with their associated character`() = runTest {
        val expectedTeamMember = User(42, "Zidun")
        val expectedGame = Game("1", "A Game", "A Description", "1")
        gameRepository.emitGames(listOf(expectedGame))

        val users = mutableListOf<User>()
        val collection1 = launch(UnconfinedTestDispatcher()) {
            teamRepository.getUsers().collect { users.add(it.last()) }
        }

        viewModel.addTeamMember(expectedTeamMember, expectedGame)

        assertTrue(users.contains(expectedTeamMember))

        collection1.cancel()

        val characters = mutableListOf<GameCharacter>()
        val collection2 = launch(UnconfinedTestDispatcher()) {
            youRepository.getCharacterForUser(expectedTeamMember, expectedGame.id.toLong()).collect {
                characters.add(it)
            }
        }

        assertTrue(characters.map { it.id }.contains(expectedTeamMember.id.toString()))

        collection2.cancel()
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
    fun `view model does not contain same user in both current team members and potential team members lists`() = runTest {
        val potentialUsers = listOf(
            User(id = 1, name = "Zidun"),
            User(id = 2, name = "Vivu"),
            User(id = 3, name = "Steinur"),
            User(id = 4, name = "Garnut")
        )
        val currentGame = CurrentGame("1", "A Game", "A Description")

        potentialUsers.take(2).forEach {
            teamRepository.addTeamMember(it, currentGame.id.toLong())
        }
        gameRepository.updateCurrentGame(currentGame)

        val users = mutableListOf<List<User>>()
        val collection = launch(UnconfinedTestDispatcher()) {
            viewModel.potentialTeamMemberList.collect {
                users.add(it)
            }
        }

        teamRepository.emitUsers(potentialUsers)

        assertEquals(potentialUsers.drop(2), users.last())

        collection.cancel()
    }

    @Test
    fun `view model does not add users to non-existent game with message`() = runTest {
        val expectedTeamMember = User(1, "Zidun")
        val nonExistentGame = Game("999", "Non-Existent Game", "It does not exist.", "1")

        var message = ""
        val collection = launch(UnconfinedTestDispatcher()) {
            viewModel.message.collect { message = it }
        }

        viewModel.addTeamMember(expectedTeamMember, nonExistentGame)
        gameRepository.emitGames(listOf(Game("1", "A Game", "A Description", "1")))

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
        val expectedGame = Game("1", "A Game", "A Description", "1")
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

    @Test
    fun `view model adds current user to game as a team member with character on initialisation if game is empty and they are the creator`() = runTest {
        val gameId = 1L
        val currentUser = dummyUserEntities.first()
        val currentGame = CurrentGame(gameId.toString(), "A Title", "A Description", currentUser.userId.toString())

        gameRepository.emitCurrentGame(currentGame)
        teamRepository.emitUsers(listOf())

        viewModel = TeamViewModel(
            GameStateUseCase(gameRepository),
            AddCurrentUserToTheirEmptyGameUseCase(teamRepository, youRepository),
            AddTeamMemberUseCase(teamRepository, youRepository),
            UserCharacterUseCase(youRepository),
            CheckGameExistsUseCase(gameRepository),
            teamRepository
        )

        val users = mutableListOf<User>()

        val collection1 = launch(UnconfinedTestDispatcher()) {
            teamRepository.getTeamMembers(gameId).collect { users.add(it.last()) }
        }

        assertTrue(users.contains(currentUser.toDomain()))

        collection1.cancel()

        val characters = mutableListOf<GameCharacter>()
        val collection2 = launch(UnconfinedTestDispatcher()) {
            youRepository.getCharacterForUser(currentUser.toDomain(), gameId).collect {
                characters.add(it)
            }
        }

        assertTrue(characters.map { it.id }.contains(currentUser.userId.toString()))

        collection2.cancel()
    }

    @Test
    fun `view model does not adds current user to game as a team member on initialisation if not the creator`() = runTest {
        val gameId = 1L
        val currentGame = CurrentGame(gameId.toString(), "A Title", "A Description", "12345")

        gameRepository.emitCurrentGame(currentGame)
        teamRepository.emitUsers(listOf())

        viewModel = TeamViewModel(
            GameStateUseCase(gameRepository),
            AddCurrentUserToTheirEmptyGameUseCase(teamRepository, youRepository),
            AddTeamMemberUseCase(teamRepository, youRepository),
            UserCharacterUseCase(youRepository),
            CheckGameExistsUseCase(gameRepository),
            teamRepository
        )

        val users = mutableListOf<User>()
        val collection = launch(UnconfinedTestDispatcher()) {
            teamRepository.getTeamMembers(gameId).collect {
                if (it.isNotEmpty()) {
                    users.add(it.last())
                }
            }
        }

        assertTrue(users.isEmpty())

        collection.cancel()
    }

    @Test
    fun `view model does not adds current user to game as a team member on initialisation if game is not empty`() = runTest {
        val gameId = 1L
        val currentUser = dummyUserEntities.first()
        val currentGame = CurrentGame(gameId.toString(), "A Title", "A Description", currentUser.userId.toString())
        val existingUser = User(gameId, "Zidun")

        gameRepository.emitCurrentGame(currentGame)
        teamRepository.addTeamMember(existingUser, 1)
        teamRepository.emitUsers(listOf(User(1, "Zidun")))

        viewModel = TeamViewModel(
            GameStateUseCase(gameRepository),
            AddCurrentUserToTheirEmptyGameUseCase(teamRepository, youRepository),
            AddTeamMemberUseCase(teamRepository, youRepository),
            UserCharacterUseCase(youRepository),
            CheckGameExistsUseCase(gameRepository),
            teamRepository
        )

        val users = mutableListOf<User>()
        val collection = launch(UnconfinedTestDispatcher()) {
            teamRepository.getTeamMembers(gameId).collect {
                if (it.isNotEmpty()) {
                    users.add(it.last())
                }
            }
        }

        assertFalse(users.contains(currentUser.toDomain()))

        collection.cancel()
    }

    @Test
    fun `view models initial dialogue state is hidden`() = runTest {
        val events = mutableListOf<TeamEvent>()
        val collection = launch(UnconfinedTestDispatcher()) {
            viewModel.event.collect {
                events.add(it)
            }
        }

        assertTrue(events.first() is TeamEvent.TeamMemberDialogueHidden)

        collection.cancel()
    }

    @Test
    fun `view model sends event for current users character when viewing requested`() = runTest {
        val currentUser = dummyUserEntities.first()
        val currentGame = CurrentGame("1", "A Title", "A Description", currentUser.userId.toString())
        val character = GameCharacter(
            id = currentUser.userId.toString(),
            userName = currentUser.username,
            character = "Zidun",
            bio = "A character bio"
        )

        gameRepository.emitCurrentGame(currentGame)
        youRepository.emitCharacters(listOf(character))

        val events = mutableListOf<TeamEvent>()
        val collection = launch(UnconfinedTestDispatcher()) {
            viewModel.event.collect {
                events.add(it)
            }
        }

        viewModel.viewCharacterForTeamMember(currentUser.toDomain())

        val lastEvent = events.last() as TeamEvent.TeamMemberDialogueShow
        assertTrue(lastEvent.teamMember == currentUser.toDomain())

        collection.cancel()
    }

    @Test
    fun `view model sends event for closing dialogue`() = runTest {
        val currentUser = dummyUserEntities.first()
        val currentGame = CurrentGame("1", "A Title", "A Description", currentUser.userId.toString())
        val character = GameCharacter(
            id = currentUser.userId.toString(),
            userName = currentUser.username,
            character = "Zidun",
            bio = "A character bio"
        )

        gameRepository.emitCurrentGame(currentGame)
        youRepository.emitCharacters(listOf(character))

        val events = mutableListOf<TeamEvent>()
        val collection = launch(UnconfinedTestDispatcher()) {
            viewModel.event.collect {
                events.add(it)
            }
        }

        viewModel.viewCharacterForTeamMember(currentUser.toDomain())
        viewModel.closeTeamMemberDialogue()

        assertEquals(3, events.size)
        assertTrue(events.first() is TeamEvent.TeamMemberDialogueHidden)
        assertTrue(events[1] is TeamEvent.TeamMemberDialogueShow)
        assertTrue(events.last() is TeamEvent.TeamMemberDialogueHidden)

        collection.cancel()
    }

    private fun CurrentGame.toGame() = Game(this.id, this.name, this.description, this.creatorId)

}
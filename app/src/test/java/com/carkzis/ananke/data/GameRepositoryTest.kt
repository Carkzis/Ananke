package com.carkzis.ananke.data

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import com.carkzis.ananke.data.database.AnankeDataStore
import com.carkzis.ananke.data.database.DefaultAnankeDataStore
import com.carkzis.ananke.data.database.GameDao
import com.carkzis.ananke.data.database.GameEntity
import com.carkzis.ananke.data.database.toDomainListing
import com.carkzis.ananke.data.model.CurrentGame
import com.carkzis.ananke.data.model.NewGame
import com.carkzis.ananke.data.repository.DefaultGameRepository
import com.carkzis.ananke.data.repository.GameRepository
import com.carkzis.ananke.testdoubles.ControllableGameDao
import com.carkzis.ananke.testdoubles.dummyGameEntities
import com.carkzis.ananke.ui.screens.game.EnterGameFailedException
import com.carkzis.ananke.ui.screens.game.ExitGameFailedException
import com.carkzis.ananke.ui.screens.game.GameDoesNotExistException
import com.carkzis.ananke.ui.screens.game.InvalidGameException
import com.carkzis.ananke.ui.screens.nugame.GameAlreadyExistsException
import com.carkzis.ananke.utils.MainDispatcherRule
import com.carkzis.ananke.utils.asGame
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.mockito.ArgumentMatchers.anyString
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class GameRepositoryTest {
    private val testScope = TestScope(UnconfinedTestDispatcher())

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @get:Rule
    val tmpFolder: TemporaryFolder = TemporaryFolder.builder().assureDeletion().build()

    private lateinit var gameRepository: GameRepository
    private lateinit var gameDao: GameDao
    private lateinit var anankeDataStore: AnankeDataStore

    private val mockAnankeDataStore: AnankeDataStore = mock()

    @Before
    fun setUp() {
        val testDataStore = PreferenceDataStoreFactory.create(
            scope = testScope,
            produceFile = { tmpFolder.newFile("test_data_store.preferences_pb") }
        )
        anankeDataStore = DefaultAnankeDataStore(testDataStore)
        gameDao = ControllableGameDao()
        gameRepository = DefaultGameRepository(gameDao, anankeDataStore)
    }

    //region games

    @Test
    fun `repository provides expected list of games`() = runTest {
        val expectedGames = getGamesEntitiesAsDomainObjects()
        val actualGames = gameRepository.getGames().first()
        assertEquals(expectedGames, actualGames)
    }

    @Test
    fun `repository adds game to database`() = runTest {
        val newGame = NewGame("aName", "aDescription")
        gameRepository.addNewGame(newGame)
        assertTrue(getGamesEntitiesAsDomainObjects().contains(newGame.asGame()))
    }

    @Test(expected = GameAlreadyExistsException::class)
    fun `repository does not add duplicate game with exception`() = runTest {
        val newGame = NewGame("aName", "aDescription")
        val newGameSameName = NewGame("aName", "aDescription")
        gameRepository.addNewGame(newGame)
        gameRepository.addNewGame(newGameSameName)
    }

    //endregion

    //region current game

    @Test
    fun `repository does not provide game data if not in a current game`() = runTest {
        val currentGame = gameRepository.getCurrentGame().first()
        val noGameId = "-1"
        assertEquals(noGameId, currentGame.id)
    }

    @Test
    fun `repository adds current game to preferences and retrieves by id`() = runTest {
        val expectedCurrentGame = CurrentGame(dummyGameEntities.first().gameId.toString())
        gameRepository.updateCurrentGame(expectedCurrentGame)

        val actualCurrentGame = gameRepository.getCurrentGame().first()
        assertEquals(expectedCurrentGame.id, anankeDataStore.currentGameId().first())
        assertEquals(expectedCurrentGame.id, actualCurrentGame.id)
    }

    @Test(expected = InvalidGameException::class)
    fun `repository cannot update current game with invalid game`() = runTest {
        val currentGame = CurrentGame("-1")
        gameRepository.updateCurrentGame(currentGame)
    }

    @Test(expected = EnterGameFailedException::class)
    fun `repository throws exception if adding current game to preference fails`() = runTest {
        whenever(mockAnankeDataStore.setCurrentGameId(anyString())).doAnswer { throw EnterGameFailedException() }
        gameRepository = DefaultGameRepository(
            gameDao,
            mockAnankeDataStore
        )
        val currentGame = CurrentGame("12345")
        gameRepository.updateCurrentGame(currentGame)
    }

    @Test(expected = GameDoesNotExistException::class)
    fun `repository throws exception if adding current game not in database`() = runTest {
        val currentGame = CurrentGame("12345")
        gameRepository.updateCurrentGame(currentGame)

        gameRepository.getCurrentGame().first()
    }

    @Test
    fun `repository removes current game from preferences`() = runTest {
        val currentGame = CurrentGame("12345")
        gameRepository.updateCurrentGame(currentGame)

        gameRepository.removeCurrentGame()

        val noGameId = "-1"
        assertEquals(noGameId, anankeDataStore.currentGameId().first())
        assertEquals(noGameId, gameRepository.getCurrentGame().first().id)
    }

    @Test(expected = ExitGameFailedException::class)
    fun `repository throws exception if removing current game from preferences fails`() = runTest {
        whenever(mockAnankeDataStore.removeCurrentGameId()).doAnswer { throw ExitGameFailedException() }
        gameRepository = DefaultGameRepository(
            gameDao,
            mockAnankeDataStore
        )
        val currentGame = CurrentGame("12345")
        gameRepository.updateCurrentGame(currentGame)

        gameRepository.removeCurrentGame()
    }

    //endregion

    private suspend fun getGamesEntitiesAsDomainObjects() =
        gameDao.getGames()
            .first()
            .map(GameEntity::toDomainListing)

}
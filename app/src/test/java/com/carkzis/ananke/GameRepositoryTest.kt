package com.carkzis.ananke

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import com.carkzis.ananke.data.AnankeDataStore
import com.carkzis.ananke.data.DefaultAnankeDataStore
import com.carkzis.ananke.data.CurrentGame
import com.carkzis.ananke.data.DefaultGameRepository
import com.carkzis.ananke.data.GameDao
import com.carkzis.ananke.data.GameEntity
import com.carkzis.ananke.data.GameRepository
import com.carkzis.ananke.data.NewGame
import com.carkzis.ananke.data.toDomainListing
import com.carkzis.ananke.testdoubles.ControllableGameDao
import com.carkzis.ananke.testdoubles.DataStoreFailure
import com.carkzis.ananke.testdoubles.FailingAnankeDataStore
import com.carkzis.ananke.testdoubles.dummyGameEntities
import com.carkzis.ananke.ui.screens.EnterGameFailedException
import com.carkzis.ananke.ui.screens.ExitGameFailedException
import com.carkzis.ananke.ui.screens.GameDoesNotExistException
import com.carkzis.ananke.ui.screens.InvalidGameException
import com.carkzis.ananke.ui.screens.nugame.GameAlreadyExistsException
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
        val expectedCurrentGame = CurrentGame(dummyGameEntities.first().id.toString())
        gameRepository.updateCurrentGame(expectedCurrentGame)

        val actualCurrentGame = gameRepository.getCurrentGame().first()
        assertEquals(expectedCurrentGame.id, anankeDataStore.currentGameId())
        assertEquals(expectedCurrentGame.id, actualCurrentGame.id)
    }

    @Test(expected = InvalidGameException::class)
    fun `repository cannot update current game with invalid game`() = runTest {
        val currentGame = CurrentGame("-1")
        gameRepository.updateCurrentGame(currentGame)
    }

    @Test(expected = EnterGameFailedException::class)
    fun `repository throws exception if adding current game to preference fails`() = runTest {
        gameRepository = DefaultGameRepository(
            gameDao,
            FailingAnankeDataStore(DataStoreFailure.ENTER_GAME)
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
        assertEquals(noGameId, anankeDataStore.currentGameId())
        assertEquals(noGameId, gameRepository.getCurrentGame().first().id)
    }

    @Test(expected = ExitGameFailedException::class)
    fun `repository throws exception if removing current game from preferences fails`() = runTest {
        gameRepository = DefaultGameRepository(
            gameDao,
            FailingAnankeDataStore(DataStoreFailure.EXIT_GAME)
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
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
import com.carkzis.ananke.data.toDomain
import com.carkzis.ananke.testdoubles.ControllableGameDao
import com.carkzis.ananke.testdoubles.FailingAnankeDataStore
import com.carkzis.ananke.ui.screens.nugame.EnterGameFailedException
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
    fun `repository adds current game to preferences`() = runTest {
        val expectedCurrentGame = CurrentGame("12345")
        gameRepository.updateCurrentGame(expectedCurrentGame)

        val actualCurrentGame = gameRepository.getCurrentGame().first()
        assertEquals(expectedCurrentGame.id, anankeDataStore.data.first())
        assertEquals(expectedCurrentGame.id, actualCurrentGame.id)
    }

    @Test(expected = EnterGameFailedException::class)
    fun `repository throws exception if adding current game to preference fails`() = runTest {
        gameRepository = DefaultGameRepository(gameDao, FailingAnankeDataStore())
        val expectedCurrentGame = CurrentGame("12345")
        gameRepository.updateCurrentGame(expectedCurrentGame)
    }

    @Test
    fun `repository removes current game from preferences`() = runTest {
        val currentGame = CurrentGame("12345")
        gameRepository.updateCurrentGame(currentGame)

        gameRepository.removeCurrentGame(currentGame)

        val noGameId = "-1"
        assertEquals(noGameId, anankeDataStore.data.first())
    }

    //endregion

    private suspend fun getGamesEntitiesAsDomainObjects() =
        gameDao.getGames()
            .first()
            .map(GameEntity::toDomain)

}
package com.carkzis.ananke.data

import com.carkzis.ananke.data.database.YouDao
import com.carkzis.ananke.data.repository.DefaultYouRepository
import com.carkzis.ananke.data.repository.YouRepository
import com.carkzis.ananke.testdoubles.ControllableYouDao
import com.carkzis.ananke.utils.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test


@OptIn(ExperimentalCoroutinesApi::class)
class YouRepositoryTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var youDao: YouDao
    private lateinit var youRepository: YouRepository

    @Before
    fun setUp() {
        youDao = ControllableYouDao()
        youRepository = DefaultYouRepository(youDao)
    }

    @Test
    fun `repository adds new character with randomised name`() = runTest {

    }

    @Test
    fun `repository adds new character to a particular game`() = runTest {

    }

    @Test
    fun `repository does not add new character if character already exists in game`() = runTest {

    }

    @Test
    fun `repository does not add new character if user already exists in game`() = runTest {

    }

    @Test
    fun `repository retrieves a particular character for current game`() = runTest {

    }

    @Test
    fun `repository updates particular character for current game`() = runTest {

    }
}
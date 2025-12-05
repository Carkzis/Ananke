package com.carkzis.ananke.ui

import com.carkzis.ananke.data.database.toDomain
import com.carkzis.ananke.data.model.User
import com.carkzis.ananke.testdoubles.ControllableYouRepository
import com.carkzis.ananke.testdoubles.dummyUserEntities
import com.carkzis.ananke.ui.screens.settings.SettingsViewModel
import com.carkzis.ananke.utils.CurrentUserUseCase
import com.carkzis.ananke.utils.MainDispatcherRule
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class SettingsViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: SettingsViewModel

    private lateinit var youRepository: ControllableYouRepository

    private val dummyCurrentUser = dummyUserEntities.first().toDomain()

    @Before
    fun setUp() {
        youRepository = ControllableYouRepository()
        youRepository.currentUser = dummyCurrentUser
        viewModel = SettingsViewModel(
            currentUserUseCase = CurrentUserUseCase(youRepository)
        )
    }

    @Test
    fun `initial state is correct`() = runTest {
        var currentUser: User? = null
        val collection = launch(UnconfinedTestDispatcher()) {
            viewModel.currentUser.collect {
                currentUser = it
            }
        }

        viewModel = SettingsViewModel(
            currentUserUseCase = CurrentUserUseCase(youRepository)
        )

        assertTrue(currentUser == dummyCurrentUser)

        collection.cancel()
    }
}
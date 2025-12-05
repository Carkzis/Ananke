package com.carkzis.ananke.ui

import com.carkzis.ananke.data.database.toDomain
import com.carkzis.ananke.data.model.User
import com.carkzis.ananke.testdoubles.ControllableYouRepository
import com.carkzis.ananke.testdoubles.dummyUserEntities
import com.carkzis.ananke.ui.screens.settings.SettingsViewModel
import com.carkzis.ananke.utils.CurrentUserUseCase
import com.carkzis.ananke.utils.MainDispatcherRule
import com.carkzis.ananke.utils.UpdateUsernameUseCase
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
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
            currentUserUseCase = CurrentUserUseCase(youRepository),
            updateUsernameUseCase = UpdateUsernameUseCase(youRepository)
        )
    }

    @Test
    fun `initial state is has current user`() = runTest {
        var currentUser: User? = null
        val collection = launch(UnconfinedTestDispatcher()) {
            viewModel.currentUser.collect {
                currentUser = it
            }
        }

        assertTrue(currentUser == dummyCurrentUser)

        collection.cancel()
    }

    @Test
    fun `updating current user's name results in repository being updated`() = runTest {
        val newName = "New Name"

        val collection = launch(UnconfinedTestDispatcher()) {
            viewModel.currentUser.collect {}
        }

        viewModel.updateCurrentUsersName(newName)

        val updatedUserFromRepository = youRepository.currentUser ?: throw AssertionError("Current user in repository is null")
        assertEquals(newName, updatedUserFromRepository.name)

        val updatedUserFromViewModel = viewModel.currentUser.value
        assertEquals(newName, updatedUserFromViewModel.name)

        collection.cancel()
    }
}

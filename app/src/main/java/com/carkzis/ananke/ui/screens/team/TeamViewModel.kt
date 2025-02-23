package com.carkzis.ananke.ui.screens.team

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carkzis.ananke.data.model.CurrentGame
import com.carkzis.ananke.data.model.Game
import com.carkzis.ananke.data.model.User
import com.carkzis.ananke.data.repository.TeamRepository
import com.carkzis.ananke.ui.screens.game.GamingState
import com.carkzis.ananke.utils.AddCurrentUserToTheirEmptyGameUseCase
import com.carkzis.ananke.utils.CheckGameExistsUseCase
import com.carkzis.ananke.utils.GameStateUseCase
import com.carkzis.ananke.utils.ValidatorResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TeamViewModel @Inject constructor(
    gameStateUseCase: GameStateUseCase,
    addCurrentUserToTheirEmptyGameUseCase: AddCurrentUserToTheirEmptyGameUseCase,
    private val checkGameExistsUseCase: CheckGameExistsUseCase,
    private val teamRepository: TeamRepository
) : ViewModel() {
    val gamingState = gameStateUseCase().stateIn(
        viewModelScope,
        WhileSubscribed(5000L),
        GamingState.Loading
    )

    val currentGame = gamingState.map {
        when (gamingState.value) {
            is GamingState.Loading, GamingState.OutOfGame -> CurrentGame.EMPTY
            is GamingState.InGame -> (gamingState.value as GamingState.InGame).currentGame
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val potentialTeamMemberList: StateFlow<List<User>> = teamRepository.getUsers().flatMapLatest { userList ->
        removeCurrentTeamMembersFromList(userList)
    }.stateIn(
        viewModelScope,
        WhileSubscribed(5000L),
        listOf()
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val currentTeamMembers = currentGame.flatMapLatest { game ->
        teamRepository.getTeamMembers(game.id.toLong())
    }.stateIn(
        viewModelScope,
        WhileSubscribed(5000L),
        listOf()
    )

    private val _message = MutableSharedFlow<String>()
    val message = _message.asSharedFlow()

    init {
        viewModelScope.launch {
            currentGame.collect {
                if (it != CurrentGame.EMPTY) {
                    addCurrentUserToTheirEmptyGameUseCase(it)
                }
            }
        }
    }

    fun addTeamMember(teamMember: User, game: Game) {
        viewModelScope.launch {
            try {
                checkGameExistsUseCase.invoke(game).collect {
                    when (it) {
                        is ValidatorResponse.Pass -> teamRepository.addTeamMember(teamMember, game.id.toLong())
                        is ValidatorResponse.Fail -> _message.emit(it.failureMessage)
                    }
                }
            } catch (e: Throwable) {
                e.message?.let {
                    _message.emit(it)
                }
            }
        }
    }

    private fun removeCurrentTeamMembersFromList(userList: List<User>) =
        currentTeamMembers.map { teamMembers ->
            userList.filter { user ->
                !teamMembers.contains(user)
            }
        }
}

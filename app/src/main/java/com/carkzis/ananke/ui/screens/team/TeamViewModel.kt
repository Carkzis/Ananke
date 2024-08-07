package com.carkzis.ananke.ui.screens.team

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carkzis.ananke.data.CurrentGame
import com.carkzis.ananke.data.Game
import com.carkzis.ananke.data.TeamRepository
import com.carkzis.ananke.data.User
import com.carkzis.ananke.ui.screens.game.GamingState
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

    val potentialTeamMemberList: StateFlow<List<User>> = teamRepository.getUsers().stateIn(
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
}

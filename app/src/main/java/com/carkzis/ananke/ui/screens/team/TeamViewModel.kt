package com.carkzis.ananke.ui.screens.team

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carkzis.ananke.data.CurrentGame
import com.carkzis.ananke.data.Game
import com.carkzis.ananke.data.GameRepository
import com.carkzis.ananke.data.TeamRepository
import com.carkzis.ananke.data.User
import com.carkzis.ananke.ui.screens.game.GamingState
import com.carkzis.ananke.utils.GameStateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TeamViewModel @Inject constructor(
    gameStateUseCase: GameStateUseCase,
    private val gameRepository: GameRepository,
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

    fun addTeamMember(teamMember: User, game: Game) {
        viewModelScope.launch {
            teamRepository.addTeamMember(teamMember, game.id.toLong())
        }
    }
}

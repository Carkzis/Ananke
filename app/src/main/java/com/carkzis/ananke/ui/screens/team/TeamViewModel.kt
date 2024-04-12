package com.carkzis.ananke.ui.screens.team

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carkzis.ananke.utils.GameStateUseCase
import com.carkzis.ananke.data.CurrentGame
import com.carkzis.ananke.ui.screens.game.GamingState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class TeamViewModel @Inject constructor(gameStateUseCase: GameStateUseCase) : ViewModel() {
    val gamingState = gameStateUseCase().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000L),
        GamingState.Loading
    )

    val currentGame = gamingState.map {
        when (gamingState.value) {
            is GamingState.Loading, GamingState.OutOfGame -> CurrentGame.EMPTY
            is GamingState.InGame -> (gamingState.value as GamingState.InGame).currentGame
        }
    }
}

package com.carkzis.ananke.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carkzis.ananke.GameStateUseCase
import com.carkzis.ananke.data.CurrentGame
import com.carkzis.ananke.data.Game
import com.carkzis.ananke.data.GameRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GameScreenViewModel @Inject constructor(
    gameStateUseCase: GameStateUseCase,
    private val gameRepository: GameRepository
) : ViewModel() {

    val gameList: StateFlow<List<Game>> = gameRepository.getGames().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000L),
        listOf()
    )

    val gamingState = gameStateUseCase().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000L),
        GamingState.OutOfGame
    )

    fun enterGame(currentGame: CurrentGame) {
        viewModelScope.launch {
            gameRepository.updateCurrentGame(currentGame)
        }
    }

    fun exitGame() {
        viewModelScope.launch {
            gameRepository.removeCurrentGame()
        }
    }
}

sealed interface GamingState {
    object OutOfGame: GamingState
    data class InGame(val currentGame: CurrentGame): GamingState
}
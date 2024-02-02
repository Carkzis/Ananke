package com.carkzis.ananke.ui.screens

import android.app.GameState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carkzis.ananke.data.CurrentGame
import com.carkzis.ananke.data.Game
import com.carkzis.ananke.data.GameRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GameScreenViewModel @Inject constructor(private val gameRepository: GameRepository) : ViewModel() {

    val gameList: StateFlow<List<Game>> = gameRepository.getGames().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000L),
        listOf()
    )

    private val _gamingState = MutableStateFlow(GamingState.OUT_OF_GAME)
    val gamingState = _gamingState.asStateFlow()

    val currentGame = gameRepository.getCurrentGame().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000L),
        CurrentGame.EMPTY
    )

    suspend fun enterGame(currentGame: CurrentGame) {
        _gamingState.value = GamingState.IN_GAME
        gameRepository.updateCurrentGame(currentGame)
    }
}

enum class GamingState {
    OUT_OF_GAME,
    IN_GAME
}
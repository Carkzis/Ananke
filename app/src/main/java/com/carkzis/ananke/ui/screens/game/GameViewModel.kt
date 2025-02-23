package com.carkzis.ananke.ui.screens.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carkzis.ananke.utils.GameStateUseCase
import com.carkzis.ananke.data.model.CurrentGame
import com.carkzis.ananke.data.model.Game
import com.carkzis.ananke.data.repository.GameRepository
import com.carkzis.ananke.utils.OnboardUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(
    gameStateUseCase: GameStateUseCase,
    onboardUserUseCase: OnboardUserUseCase,
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
        GamingState.Loading
    )

    private val _message = MutableSharedFlow<String>()
    val message = _message.asSharedFlow()

    init {
        viewModelScope.launch {
            onboardUserUseCase()
        }
    }

    fun enterGame(currentGame: CurrentGame) {
        viewModelScope.launch {
            try {
                gameRepository.updateCurrentGame(currentGame)
            } catch (exception: Throwable) {
                when (exception) {
                    is InvalidGameException,
                    is GameDoesNotExistException,
                    is EnterGameFailedException -> {
                        exception.message?.let { _message.emit(it) }
                    }
                    else -> throw Exception(exception.message)
                }
            }
        }
    }

    fun exitGame() {
        viewModelScope.launch {
            try {
                gameRepository.removeCurrentGame()
            } catch (exception: ExitGameFailedException) {
                exception.message.let { _message.emit(it) }
            }
        }
    }
}

sealed interface GamingState {
    object OutOfGame: GamingState
    object Loading : GamingState
    data class InGame(val currentGame: CurrentGame): GamingState
}
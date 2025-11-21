package com.carkzis.ananke.ui.screens.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carkzis.ananke.data.model.CurrentGame
import com.carkzis.ananke.data.model.Game
import com.carkzis.ananke.data.model.GameWithPlayerCount
import com.carkzis.ananke.data.repository.GameRepository
import com.carkzis.ananke.utils.CleanUpCharactersAndTeamMembersUseCase
import com.carkzis.ananke.utils.DeletableGameUseCase
import com.carkzis.ananke.utils.GameStateUseCase
import com.carkzis.ananke.utils.OnboardUserUseCase
import com.carkzis.ananke.utils.PlayerCountUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(
    gameStateUseCase: GameStateUseCase,
    onboardUserUseCase: OnboardUserUseCase,
    deletableGameUseCase: DeletableGameUseCase,
    private val playerCountUseCase: PlayerCountUseCase,
    private val cleanUpCharactersAndTeamMembersUseCase: CleanUpCharactersAndTeamMembersUseCase,
    private val gameRepository: GameRepository
) : ViewModel() {

    val gameList: StateFlow<List<Game>> = gameRepository.getGames().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000L),
        listOf()
    )

    val playerCountForGames = MutableStateFlow<List<GameWithPlayerCount>>(listOf())

    val deletableGames = gameList.map {
        it.filter { game ->
            deletableGameUseCase(game)
        }
    }.stateIn(
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

    fun onViewDisplayed() {
        viewModelScope.launch {
            val games = gameList.value
            val gamesWithPlayerCounts = games.map { game ->
                val playerCount = playerCountUseCase(game.id.toLong())
                GameWithPlayerCount(
                    game = game,
                    playerCount = playerCount
                )
            }
            playerCountForGames.emit(gamesWithPlayerCounts)
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

    fun deleteGame(game: Game) {
        viewModelScope.launch {
            try {
                gameRepository.deleteGame(game)
                cleanUpCharactersAndTeamMembersUseCase(game.id.toLong())
            } catch (exception: Throwable) {
                when (exception) {
                    is CreatorIdDoesNotMatchException,
                    is GameDoesNotExistException -> {
                        exception.message?.let { _message.emit(it) }
                    }
                    else -> throw Exception(exception.message)
                }
            }
        }
    }
}

sealed interface GamingState {
    object OutOfGame: GamingState
    object Loading : GamingState
    data class InGame(val currentGame: CurrentGame): GamingState
}
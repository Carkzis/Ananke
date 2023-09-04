package com.carkzis.ananke.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carkzis.ananke.data.GameRepository
import com.carkzis.ananke.data.NewGame
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewGameScreenViewModel @Inject constructor(private val gameRepository: GameRepository) : ViewModel() {

    private val _gameTitle = MutableStateFlow("")
    val gameTitle = _gameTitle.asStateFlow()

    fun setGameTitle(title: String) {
        _gameTitle.value = title
    }

    fun addNewGame(newGame: NewGame) {
        viewModelScope.launch {
            gameRepository.addNewGame(newGame)
        }
    }

}
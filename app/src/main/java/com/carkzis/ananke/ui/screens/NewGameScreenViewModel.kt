package com.carkzis.ananke.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carkzis.ananke.data.Game
import com.carkzis.ananke.data.GameRepository
import com.carkzis.ananke.data.NewGame
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewGameScreenViewModel @Inject constructor(private val gameRepository: GameRepository) : ViewModel() {

    fun addNewGame(newGame: NewGame) {
        viewModelScope.launch {
            gameRepository.addNewGame(newGame)
        }
    }

}
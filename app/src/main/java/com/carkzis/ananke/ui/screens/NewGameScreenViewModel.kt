package com.carkzis.ananke.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carkzis.ananke.data.Game
import com.carkzis.ananke.data.GameRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewGameScreenViewModel @Inject constructor(private val gameRepository: GameRepository) : ViewModel() {

    fun addGame(game: Game) {
        viewModelScope.launch {
            gameRepository.addGame(game)
        }
    }

}
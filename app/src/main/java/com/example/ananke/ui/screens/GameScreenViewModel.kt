package com.example.ananke.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class GameScreenViewModel @Inject constructor() : ViewModel() {

    val gamesList: StateFlow<List<String>> = flowOf(listOf<String>()).stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000L),
        listOf("Game 1")
    )
}
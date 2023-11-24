package com.carkzis.ananke.testdoubles

import com.carkzis.ananke.ui.screens.nugame.NewGameViewModel

class DummyNewGameViewModel : NewGameViewModel(gameRepository = DummyGameRepository())
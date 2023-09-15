package com.carkzis.ananke.testdoubles

import com.carkzis.ananke.ui.screens.NewGameViewModel

class DummyNewGameViewModel : NewGameViewModel(gameRepository = DummyGameRepository())
package com.carkzis.ananke

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.carkzis.ananke.data.GameRepository
import com.carkzis.ananke.ui.AnankeApp
import com.carkzis.ananke.ui.theme.AnankeTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var gameRepository: GameRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AnankeTheme {
                AnankeApp(gameRepository = gameRepository)
            }
        }
    }
}
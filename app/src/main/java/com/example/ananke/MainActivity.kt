package com.carkzis.ananke

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.carkzis.ananke.ui.AnankeApp
import com.carkzis.ananke.ui.theme.AnankeTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AnankeTheme {
                AnankeApp()
            }
        }
    }
}
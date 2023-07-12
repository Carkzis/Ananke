package com.example.ananke

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AnankeBackground(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    // A surface container using the 'background' color from the theme
    Surface(
        modifier = modifier.fillMaxSize().padding(8.dp),
        color = MaterialTheme.colorScheme.background
    ) {
        content()
    }
}
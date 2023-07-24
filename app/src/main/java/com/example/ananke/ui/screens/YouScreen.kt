package com.example.ananke.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ananke.ui.components.AnankeText

@Composable
fun YouScreen(modifier: Modifier = Modifier) {
    AnankeText(text = "You", modifier = modifier.padding(8.dp))
}
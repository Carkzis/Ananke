package com.example.ananke

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun AnankeBottomBar(modifier: Modifier, content: @Composable RowScope.() -> Unit) {
    NavigationBar(
        modifier = modifier,
        containerColor = Color.Green,
        content = content
    )
}

@Composable
fun RowScope.AnankeNavigationItem(modifier: Modifier, icon: @Composable () -> Unit) {
    NavigationBarItem(
        modifier = modifier,
        icon = icon,
        onClick = {},
        selected = false
    )
}
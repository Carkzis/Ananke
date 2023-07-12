package com.example.ananke

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavDestination

@Composable
fun AnankeBottomBar(
    modifier: Modifier,
    destinations: List<AnankeDestinations>,
    currentDestination: NavDestination?,
    onNavigate: (AnankeDestinations) -> Unit
) {

    NavigationBar(
        modifier = modifier,
        containerColor = Color.Green
    ) {
        destinations.forEach { destination ->
            AnankeNavigationItem(modifier = modifier) {
                Icon(imageVector = destination.icon, contentDescription = null)
            }
        }
    }
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
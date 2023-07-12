package com.example.ananke

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy

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
            val isCurrentlySelected = currentDestination?.route?.contains(destination.name, false) ?: false
            AnankeNavigationItem(selected = isCurrentlySelected,
                selectedIcon = { Icon(imageVector = Icons.Rounded.ArrowDropDown, contentDescription = null) },
                icon = { Icon(imageVector = destination.icon, contentDescription = null) },
                modifier = modifier)
        }
    }
}

@Composable
fun RowScope.AnankeNavigationItem(selected: Boolean, modifier: Modifier, selectedIcon: @Composable () -> Unit, icon: @Composable () -> Unit) {
    NavigationBarItem(
        selected = selected,
        modifier = modifier,
        icon = if (selected) selectedIcon else icon,
        onClick = {}
    )
}
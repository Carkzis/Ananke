package com.carkzis.ananke.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Games
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavDestination
import com.carkzis.ananke.navigation.AnankeDestination

@Composable
fun AnankeBottomBar(
    modifier: Modifier = Modifier,
    destinations: List<AnankeDestination>,
    currentDestination: NavDestination?,
    availabilities: Map<AnankeDestination, Boolean>,
    onNavigate: (AnankeDestination) -> Unit
) {
    NavigationBar(
        modifier = modifier,
        containerColor = Color(color = 0xFF77DD77)
    ) {
        destinations.forEach { destination ->
            val isCurrentlySelected =
                currentDestination?.route?.contains(destination.name, false) ?: false
            val isAvailable = availabilities.getOrDefault(destination, false)
            AnankeNavigationItem(
                selected = isCurrentlySelected,
                onClick = { onNavigate(destination) },
                selectedIcon = {
                    Icon(
                        imageVector = destination.selectedIcon,
                        contentDescription = null
                    )
                },
                icon = {
                    Icon(
                        imageVector = destination.unselectedIcon,
                        contentDescription = null
                    )
                },
                enabled = isAvailable,
                modifier = modifier.testTag(tag = "$destination-navigation-item")
            )
        }
    }
}

@Composable
fun RowScope.AnankeNavigationItem(
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    enabled: Boolean,
    selectedIcon: @Composable () -> Unit,
    icon: @Composable () -> Unit
) {
    NavigationBarItem(
        selected = selected,
        modifier = modifier,
        enabled = enabled,
        icon = if (selected) selectedIcon else icon,
        onClick = onClick
    )
}

@Preview
@Composable
fun BottomBar() {
    AnankeBottomBar(
        destinations = AnankeDestination.values().toList(),
        currentDestination = null,
        onNavigate = {},
        availabilities = AnankeDestination.values().associateWith { true }
    )
}

@Preview(widthDp = 100, heightDp = 100)
@Composable
fun NavigationItemSelected() {
    Row(modifier = Modifier
        .wrapContentSize()
        .background(Color(color = 0xFF77DD77))) {
        AnankeNavigationItem(
            selected = true,
            onClick = {},
            selectedIcon = { Icon(imageVector = Icons.Filled.Games, null) },
            enabled = true,
            icon = { Icon(imageVector = Icons.Filled.Games, null) })
    }
}

@Preview(widthDp = 100, heightDp = 100)
@Composable
fun NavigationItemUnselected() {
    Row(modifier = Modifier
        .wrapContentSize()
        .background(Color(color = 0xFF77DD77))) {
        AnankeNavigationItem(
            selected = false,
            onClick = {},
            selectedIcon = { Icon(imageVector = Icons.Filled.Games, null) },
            enabled = true,
            icon = { Icon(imageVector = Icons.Filled.Games, null) })
    }
}
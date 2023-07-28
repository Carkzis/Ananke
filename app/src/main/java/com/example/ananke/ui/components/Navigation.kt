package com.example.ananke.ui.components

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
import com.example.ananke.navigation.AnankeDestination

@Composable
fun AnankeBottomBar(
    modifier: Modifier = Modifier,
    destinations: List<AnankeDestination>,
    currentDestination: NavDestination?,
    onNavigate: (AnankeDestination) -> Unit
) {
    NavigationBar(
        modifier = modifier,
        containerColor = Color.Green
    ) {
        destinations.forEach { destination ->
            val isCurrentlySelected =
                currentDestination?.route?.contains(destination.name, false) ?: false
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
    selectedIcon: @Composable () -> Unit,
    icon: @Composable () -> Unit
) {
    NavigationBarItem(
        selected = selected,
        modifier = modifier,
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
        onNavigate = {})
}

@Preview(widthDp = 100, heightDp = 100)
@Composable
fun NavigationItemSelected() {
    Row(modifier = Modifier
        .wrapContentSize()
        .background(Color.Green)) {
        AnankeNavigationItem(
            selected = true,
            onClick = {},
            selectedIcon = { Icon(imageVector = Icons.Filled.Games, null) },
            icon = { Icon(imageVector = Icons.Filled.Games, null) })
    }
}

@Preview(widthDp = 100, heightDp = 100)
@Composable
fun NavigationItemUnselected() {
    Row(modifier = Modifier
        .wrapContentSize()
        .background(Color.Green)) {
        AnankeNavigationItem(
            selected = false,
            onClick = {},
            selectedIcon = { Icon(imageVector = Icons.Filled.Games, null) },
            icon = { Icon(imageVector = Icons.Filled.Games, null) })
    }
}
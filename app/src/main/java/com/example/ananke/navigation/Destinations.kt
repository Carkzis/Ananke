package com.example.ananke.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Games
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

enum class AnankeDestination(
    val unselectedIcon: ImageVector,
    val selectedIcon: ImageVector
) {
    GAME(unselectedIcon = Icons.Filled.Games, selectedIcon = Icons.Filled.Games),
    TEAM(unselectedIcon = Icons.Filled.Groups, selectedIcon = Icons.Filled.Groups),
    YOU(unselectedIcon = Icons.Filled.Person, selectedIcon = Icons.Filled.Person)
}
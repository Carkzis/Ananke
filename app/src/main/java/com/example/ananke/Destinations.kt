package com.example.ananke

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountBox
import androidx.compose.material.icons.rounded.Call
import androidx.compose.material.icons.rounded.Create
import androidx.compose.ui.graphics.vector.ImageVector

enum class AnankeDestination(
    val icon: ImageVector
) {
    SCREEN_ONE(icon = Icons.Rounded.Create),
    SCREEN_TWO(icon = Icons.Rounded.AccountBox),
    SCREEN_THREE(icon = Icons.Rounded.Call)
}
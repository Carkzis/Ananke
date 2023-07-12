package com.example.ananke

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountBox
import androidx.compose.material.icons.rounded.Call
import androidx.compose.material.icons.rounded.Create
import androidx.compose.ui.graphics.vector.ImageVector

enum class AnankeDestinations(
    val icon: ImageVector
) {
    CREATE(icon = Icons.Rounded.Create),
    ACCOUNT(icon = Icons.Rounded.AccountBox),
    CALL(icon = Icons.Rounded.Call)
}
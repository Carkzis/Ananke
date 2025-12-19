package com.carkzis.ananke.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.carkzis.ananke.navigation.AnankeDestination

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnankeTopBar(
    searchEnabled: Boolean = true,
    onSearchClicked: () -> Unit = {},
    onNavigate: (AnankeDestination) -> Unit = {}
) {
    CenterAlignedTopAppBar(
        title = {
            AnankeText(
                text = "Ananke",
                modifier = Modifier.padding(8.dp),
                textStyle = MaterialTheme.typography.headlineLarge
            )
        },
        navigationIcon = {
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = null,
                tint = if (searchEnabled) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.38f)
                },
                modifier = Modifier.padding(16.dp)
                    .clickable(
                        enabled = searchEnabled
                    ) {
                        onSearchClicked()
                    }
                    .testTag("global-search-button")
            )
        },
        actions = {
            Icon(
                imageVector = Icons.Filled.Settings,
                contentDescription = null,
                modifier = Modifier.padding(16.dp)
                    .clickable {
                        onNavigate(AnankeDestination.SETTINGS)
                    }
                    .testTag("${AnankeDestination.SETTINGS}-navigation-item")
            )
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color(0xFFB7FFFA)),
        modifier = Modifier
    )
}

@Preview
@Composable
fun TopBar() {
    AnankeTopBar()
}
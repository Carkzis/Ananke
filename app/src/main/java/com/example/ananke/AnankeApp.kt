package com.example.ananke

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnankeApp(
    appState: AnankeAppState = rememberAnankeAppState()
) {
    AnankeBackground {
        Scaffold(
            modifier = Modifier,
            topBar = {
                     AnankeTopBar()
            },
            bottomBar = {
                AnankeBottomBar(modifier = Modifier,
                    destinations = appState.destinations,
                    currentDestination = appState.currentDestination,
                    onNavigate = appState::navigateToDestination
                )
            }
        ) { padding ->
            Column(modifier = Modifier.padding(padding)) {
                AnankeNavHost(appState = appState)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnankeTopBar() {
    CenterAlignedTopAppBar(
        title = { AnankeText(text = "Ananke", modifier = Modifier.padding(8.dp)) },
        navigationIcon = { Icon(imageVector = Icons.Filled.Search, contentDescription = null, modifier = Modifier.padding(16.dp)) },
        actions = { Icon(imageVector = Icons.Filled.Settings, contentDescription = null, modifier = Modifier.padding(16.dp)) },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Cyan),
        modifier = Modifier
    )
}

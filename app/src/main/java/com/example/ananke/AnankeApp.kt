package com.example.ananke

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
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
            bottomBar = {
                AnankeBottomBar(modifier = Modifier,
                    destinations = appState.destinations,
                    currentDestination = appState.currentDestination,
                    onNavigate = appState::navigateToDestination
                )
            }
        ) { padding ->
            Column(modifier = Modifier.padding(padding)) {
                DummyHeader()
                AnankeNavHost(appState = appState)
            }
        }
    }
}

@Composable
private fun DummyHeader() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(8.dp)
            .background(Color.Cyan)
            .fillMaxWidth(),
    ) {
        AnankeText(text = "Ananke", modifier = Modifier.padding(8.dp))
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(8.dp)
            .background(Color.Cyan)
            .fillMaxWidth(),
    ) {
        AnankeText(text = "The pain of UI begins!", modifier = Modifier.padding(8.dp))
    }
}

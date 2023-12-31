package com.carkzis.ananke.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.carkzis.ananke.navigation.AnankeDestination
import com.carkzis.ananke.ui.components.AnankeText

@Composable
fun TeamScreen(modifier: Modifier = Modifier) {
    AnankeText(text = "Team", modifier = modifier.padding(8.dp).testTag("${AnankeDestination.TEAM}-title"))
}
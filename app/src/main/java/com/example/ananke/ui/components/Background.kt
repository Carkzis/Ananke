package com.example.ananke.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ananke.ui.theme.AnankeTheme

@Composable
fun AnankeBackground(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    // A surface container using the 'background' color from the theme
    Surface(
        modifier = modifier.fillMaxSize(),
        tonalElevation = 3.dp
    ) {
        content()
    }
}

@ThemePreviews
@Composable
fun DefaultBackground() {
    AnankeTheme(dynamicColor = false) {
        AnankeBackground(Modifier.size(100.dp)) {}
    }
}

@ThemePreviews
@Composable
fun DynamicBackground() {
    AnankeTheme(dynamicColor = true) {
        AnankeBackground(Modifier.size(100.dp)) {}
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, name = "Light Theme")
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Dark Theme")
annotation class ThemePreviews
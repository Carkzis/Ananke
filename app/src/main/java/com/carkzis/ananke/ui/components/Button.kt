package com.carkzis.ananke.ui.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.carkzis.ananke.navigation.GameDestination

@Composable
fun AnankeButton(onClick: () -> Unit, modifier: Modifier = Modifier, content: @Composable RowScope.() -> Unit) {
    Button(
        onClick = onClick,
        modifier = modifier.padding(8.dp),
        content = content
    )
}

@Preview(showBackground = true)
@Composable
fun AnankeButtonPreview() {
    AnankeButton(onClick = {}) {
        AnankeText(
            text = "A button!"
        )
    }
}
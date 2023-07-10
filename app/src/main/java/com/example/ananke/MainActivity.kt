package com.example.ananke

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ananke.ui.theme.AnankeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AnankeTheme {
                val modifier = Modifier.padding(8.dp)
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(modifier = modifier) {
                        AnankeRow(modifier = modifier) {
                            AnankeText(text = "Ananke", modifier = modifier)
                        }
                        AnankeRow(modifier = modifier) {
                            AnankeText(text = "The pain of UI begins!", modifier = modifier)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AnankeText(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        textAlign = TextAlign.Center,
        modifier = modifier.fillMaxWidth()
    )
}

@Composable
fun AnankeRow(modifier: Modifier = Modifier, content: @Composable RowScope.() -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .background(Color.Cyan)
            .fillMaxWidth(),
        content = content
    )
}

@Preview(showBackground = true)
@Composable
fun BeginnerPreview() {
    AnankeTheme {
        val modifier = Modifier.padding(8.dp)
        AnankeRow(modifier = modifier) {
            AnankeText(text = "Ananke", modifier = modifier)
        }
    }
}
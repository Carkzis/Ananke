package com.carkzis.ananke.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.carkzis.ananke.ui.theme.Typography

@Composable
fun AnankeText(
    text: String,
    modifier: Modifier = Modifier,
    textAlign: TextAlign = TextAlign.Center,
    textStyle: TextStyle = Typography.bodyMedium,
) {
    Text(
        text = text,
        textAlign = textAlign,
        style = textStyle,
        modifier = modifier.fillMaxWidth()
    )
}

@Composable
fun AnankeTextField(
    modifier: Modifier = Modifier,
    lines: Int = 1,
    value: String,
    readOnly: Boolean = false,
    onValueChange: (String) -> Unit,
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        shape = RoundedCornerShape(16.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Green,
            unfocusedContainerColor = Color.Green,
            focusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        minLines = lines,
        maxLines = lines,
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    )
}

@Composable
fun AnankeMediumTitleText(modifier: Modifier = Modifier, text: String) {
    AnankeText(
        modifier = modifier,
        text = text,
        textStyle = MaterialTheme.typography.titleMedium
    )
}

@Preview(showBackground = true)
@Composable
fun AnankeTextPreview() {
    AnankeText(text = "Hello world!")
}

@Preview(showBackground = true)
@Composable
fun AnankeTextFieldOneLinePreview() {
    AnankeTextField(
        value = "Hello world!",
        lines = 1,
        onValueChange = {},
    )
}

@Preview(showBackground = true)
@Composable
fun AnankeTextFieldThreeLinePreview() {
    AnankeTextField(
        value = "Hello world!",
        lines = 3,
        onValueChange = {},
    )
}

@Preview(showBackground = true)
@Composable
fun AnankeMediumTitleText() {
    AnankeMediumTitleText(text = "Hello world!")
}
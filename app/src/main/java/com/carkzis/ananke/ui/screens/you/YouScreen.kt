package com.carkzis.ananke.ui.screens.you

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.carkzis.ananke.data.model.CurrentGame
import com.carkzis.ananke.navigation.AnankeDestination
import com.carkzis.ananke.ui.components.AnankeButton
import com.carkzis.ananke.ui.components.AnankeText
import com.carkzis.ananke.ui.components.AnankeTextField
import com.carkzis.ananke.ui.screens.game.GamingState
import com.carkzis.ananke.ui.theme.AnankeTheme

@Composable
fun YouScreen(
    currentGame: CurrentGame,
    gamingState: GamingState,
    characterName: String,
    characterBio: String,
    onTitleValueChanged: (String, Boolean) -> Unit,
    onBioValueChanged: (String, Boolean) -> Unit,
    modifier: Modifier = Modifier,
    onEnableEditCharacterName: () -> Unit,
    onEnableEditCharacterBio: () -> Unit,
) {
    when (gamingState) {
        is GamingState.Loading -> {}
        is GamingState.OutOfGame -> {}
        is GamingState.InGame -> {
            Column {
                AnankeText(
                    text = "You",
                    modifier = modifier
                        .padding(8.dp)
                        .testTag("${AnankeDestination.YOU}-title"),
                    textStyle = MaterialTheme.typography.headlineMedium
                )

                AnankeText(
                    text = currentGame.name,
                    modifier = modifier
                        .padding(8.dp)
                        .testTag("${AnankeDestination.YOU}-current-game"),
                    textStyle = MaterialTheme.typography.headlineSmall
                )

                CharacterName(
                    characterName,
                    onTitleValueChanged,
                    onEnableEditCharacterName,
                    modifier
                )

                CharacterBio(
                    characterBio,
                    onBioValueChanged,
                    onEnableEditCharacterBio,
                    modifier
                )
            }
        }
    }
}

@Composable
private fun CharacterName(
    characterName: String,
    onTitleValueChanged: (String, Boolean) -> Unit,
    onEnableEditCharacterName: () -> Unit,
    modifier: Modifier
) {
    YouAttribute("name", characterName, onTitleValueChanged, onEnableEditCharacterName, modifier)
}

@Composable
private fun CharacterBio(
    characterBio: String,
    onBioValueChanged: (String, Boolean) -> Unit,
    onEnableEditCharacterBio: () -> Unit,
    modifier: Modifier
) {
    YouAttribute("bio", characterBio, onBioValueChanged, onEnableEditCharacterBio, modifier)
}

@Composable
private fun YouAttribute(
    attributeType: String,
    attributeText: String,
    onValueChanged: (String, Boolean) -> Unit,
    onEnableEdit: () -> Unit,
    modifier: Modifier
) {
    var characterAttributeIsEditable by remember { mutableStateOf(false) }

    AnankeText(
        text = "Character Name:",
    )
    AnankeTextField(
        value = attributeText,
        onValueChange = {
            onValueChanged(it, characterAttributeIsEditable)
        },
        readOnly = characterAttributeIsEditable,
        modifier = Modifier
            .testTag("${AnankeDestination.YOU}-character-$attributeType")
    )
    AnankeButton(
        modifier = Modifier
            .testTag("${AnankeDestination.YOU}-edit-$attributeType-button"),
        onClick = {
            onEnableEdit()
            characterAttributeIsEditable = true
        }
    ) {
        AnankeText(
            text = "Edit",
            modifier = modifier
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun YouScreenPreview() {
    AnankeTheme {
        val currentGame = CurrentGame(
            id = "1",
            name = "Preview Game",
            description = "This is not a real game."
        )
        YouScreen(
            currentGame = currentGame,
            gamingState = GamingState.InGame(currentGame),
            onTitleValueChanged = { _, _ -> },
            onBioValueChanged = { _, _ -> },
            characterName = "",
            characterBio = "",
            onEnableEditCharacterName = {},
            onEnableEditCharacterBio = {},
        )
    }
}
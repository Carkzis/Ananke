package com.carkzis.ananke.ui.screens.team

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.carkzis.ananke.data.model.CurrentGame
import com.carkzis.ananke.data.model.toGame
import com.carkzis.ananke.ui.screens.game.GamingState

@Composable
fun TeamRoute(
    modifier: Modifier = Modifier,
    viewModel: TeamViewModel = hiltViewModel(),
    onOutOfGame: () -> Unit,
    onShowSnackbar: suspend (String) -> Boolean,
) {
    val currentGame by viewModel.currentGame.collectAsStateWithLifecycle(CurrentGame.EMPTY)
    val gameState by viewModel.gamingState.collectAsStateWithLifecycle()
    val availableUsers by viewModel.potentialTeamMemberList.collectAsStateWithLifecycle()
    val teamMembers by viewModel.currentTeamMembers.collectAsStateWithLifecycle()
    val events by viewModel.event.collectAsStateWithLifecycle()

    if (gameState == GamingState.OutOfGame) {
        onOutOfGame()
    }

    TeamScreen(
        modifier = modifier,
        currentGame = currentGame,
        gamingState = gameState,
        users = availableUsers,
        teamMembers = teamMembers,
        event = events,
        onAddUser = { user ->
            viewModel.addTeamMember(user, currentGame.toGame())
        },
        onViewTeamMember = { teamMember ->
            viewModel.viewCharacterForTeamMember(teamMember)
        },
        onViewUser = { user ->
            viewModel.viewUser(user)
        },
        onDismissDialogue = {
            viewModel.closeDialogue()
        },
        onShowSnackbar = {
            viewModel.message.collect {
                onShowSnackbar(it)
            }
        },
        onRemoveTeamMember = { teamMember ->
            viewModel.deleteTeamMember(teamMember)
        },
        onViewTeamMemberForRemoval = { teamMember ->
            viewModel.deleteTeamMemberDialogue(teamMember)
        }
    )
}
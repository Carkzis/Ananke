package com.carkzis.ananke.ui.screens.team

import com.carkzis.ananke.data.model.GameCharacter
import com.carkzis.ananke.data.model.User

sealed class TeamEvent {
    data class TeamMemberDialogueShow(val teamMember: User, val character: GameCharacter) : TeamEvent()
    data class UserDialogueShow(val user: User) : TeamEvent()
    data class DeleteTeamMemberConfirmationDialogueShow(val teamMember: User) : TeamEvent()
    data object CloseDialogue : TeamEvent()
}
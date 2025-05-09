package com.carkzis.ananke.ui.screens.team

import com.carkzis.ananke.data.model.GameCharacter
import com.carkzis.ananke.data.model.User

sealed class TeamEvent {
    data class TeamMemberDialogueShow(val teamMember: User, val character: GameCharacter) : TeamEvent()
    data object TeamMemberDialogueHidden : TeamEvent()
}
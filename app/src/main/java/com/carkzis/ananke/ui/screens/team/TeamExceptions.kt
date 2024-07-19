package com.carkzis.ananke.ui.screens.team

class UserAlreadyExistsException : Throwable() {
    override val message = "A user already exists for that ID."
}

class TooManyUsersInTeamException(teamMemberLimit: Int) : Throwable() {
    override val message = "Failed to add team member, limit of $teamMemberLimit exceeded."
}
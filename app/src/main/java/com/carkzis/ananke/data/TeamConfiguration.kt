package com.carkzis.ananke.data

const val DEFAULT_TEAM_SIZE = 4

data class TeamConfiguration(var teamMemberLimit: Int = DEFAULT_TEAM_SIZE)
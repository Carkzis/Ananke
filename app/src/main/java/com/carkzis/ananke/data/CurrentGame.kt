package com.carkzis.ananke.data

// TODO: Need all the domain game info.
data class CurrentGame(
    val id: String
) {
    companion object {
        val EMPTY = CurrentGame("-1")
    }
}
package com.carkzis.ananke.data

data class CurrentGame(
    val id: String
) {
    companion object {
        val EMPTY = CurrentGame("-1")
    }
}
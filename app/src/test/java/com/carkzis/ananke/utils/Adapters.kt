package com.carkzis.ananke.utils

import com.carkzis.ananke.data.Game
import com.carkzis.ananke.data.NewGame

fun NewGame.asGame(id: Long = 0L) = Game(
    id = id.toString(),
    name = name,
    description = description
)
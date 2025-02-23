package com.carkzis.ananke.utils

import com.carkzis.ananke.data.model.Game
import com.carkzis.ananke.data.model.NewGame

fun NewGame.asGame(id: Long = 0L) = Game(
    id = id.toString(),
    name = name,
    description = description,
    creatorId = creatorId.toString()
)
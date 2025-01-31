package com.carkzis.ananke.testdoubles

import com.carkzis.ananke.data.database.CharacterEntity
import com.carkzis.ananke.data.database.GameEntity
import com.carkzis.ananke.data.database.UserEntity

val dummyGameEntities = listOf(
    GameEntity(1L, "My First Game", "It is the first one."),
    GameEntity(2L, "My Second Game", "It is the second one."),
    GameEntity(3L, "My Third Game", "It is the third one.")
)

val dummyUserEntities = listOf(
    UserEntity(4L, "Alpha"),
    UserEntity(5L, "Beta"),
    UserEntity(6L, "Gamma")
)

val dummyCharacterEntities = listOf(
    CharacterEntity(7L, 4L, 1L,"Beep", "It beeps."),
    CharacterEntity(8L, 4L, 1L, "Boop", "It boops."),
    CharacterEntity(9L, 4L, 1L, "Jeremy Stevens", "It hungers."),
)
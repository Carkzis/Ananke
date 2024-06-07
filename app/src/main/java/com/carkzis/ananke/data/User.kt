package com.carkzis.ananke.data

data class User(
    val id: Long,
    val name: String,
)

fun User.toEntity(gameIds: List<Long>) = UserEntity(
    id = id,
    name = name,
    gameIds = gameIds
)
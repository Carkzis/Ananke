package com.carkzis.ananke.data

data class User(
    val id: Long,
    val name: String,
)

fun User.toEntity() = UserEntity(
    userId = id,
    username = name
)
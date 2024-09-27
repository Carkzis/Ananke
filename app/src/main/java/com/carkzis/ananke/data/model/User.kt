package com.carkzis.ananke.data.model

import com.carkzis.ananke.data.database.UserEntity

data class User(
    val id: Long,
    val name: String,
)

fun User.toEntity() = UserEntity(
    userId = id,
    username = name
)
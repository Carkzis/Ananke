package com.carkzis.ananke.data.network

import com.carkzis.ananke.data.model.User

data class NetworkUser(
    val id: Long,
    val name: String,
)

fun NetworkUser.toDomainUser() = User(
    id = id,
    name = name
)

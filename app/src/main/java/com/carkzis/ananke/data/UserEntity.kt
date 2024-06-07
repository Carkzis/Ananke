package com.carkzis.ananke.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "users"
)
data class UserEntity(
    @PrimaryKey
    val id: Long,
    val name: String,
    val gameIds: List<Long>
)

fun UserEntity.toDomain() = User(
    id = id,
    name = name
)
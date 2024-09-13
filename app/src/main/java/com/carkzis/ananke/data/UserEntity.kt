package com.carkzis.ananke.data

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Junction
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(
    tableName = "users"
)
data class UserEntity(
    @PrimaryKey
    val userId: Long,
    val username: String
)

@Entity(primaryKeys = ["gameId", "userId"])
data class UserGameCrossRef(
    val gameId: Long,
    val userId: Long
)

data class UserEntityWithGames(
    @Embedded val user: UserEntity,
    @Relation(
        parentColumn = "userId",
        entityColumn = "gameId",
        associateBy = Junction(UserGameCrossRef::class)
    )
    val games: List<GameEntity>
)

fun UserEntityWithGames.toDomain() = this.user.toDomain()

fun UserEntity.toDomain() = User(
    id = userId,
    name = username
)
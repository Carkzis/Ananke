package com.carkzis.ananke.data.database

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Junction
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.carkzis.ananke.data.model.GameCharacter

@Entity(
    tableName = "characters"
)
data class CharacterEntity(
    @PrimaryKey(autoGenerate = true)
    val characterId: Long = 0L,
    val userOwnerId: Long,
    val gameOwnerId: Long,
    val characterName: String,
    val characterBio: String
)


data class UserEntityWithCharacters(
    @Embedded val userEntity: UserEntity,
    @Relation(
        parentColumn = "userId",
        entityColumn = "userOwnerId",
    )
    val characterEntities: List<CharacterEntity>
)

data class GameEntityWithCharacters(
    @Embedded val gameEntity: GameEntity,
    @Relation(
        parentColumn = "gameId",
        entityColumn = "gameOwnerId",
    )
    val characterEntities: List<CharacterEntity>
)

fun CharacterEntity.toCharacter(userName: String) = GameCharacter(
    id = characterId.toString(),
    userName = userName,
    character = characterName,
    bio = characterBio
)
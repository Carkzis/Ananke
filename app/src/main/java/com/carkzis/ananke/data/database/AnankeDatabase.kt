package com.carkzis.ananke.data.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        GameEntity::class,
        UserEntity::class,
        UserGameCrossRef::class,
        CharacterEntity::class,
               ],
    version = 1,
    exportSchema = true
)
abstract class AnankeDatabase : RoomDatabase() {
    abstract fun gameDao(): GameDao
    abstract fun teamDao(): TeamDao
    abstract fun youDao(): YouDao
}
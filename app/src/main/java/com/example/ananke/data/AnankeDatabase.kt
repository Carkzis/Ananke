package com.example.ananke.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [GameEntity::class],
    version = 1,
    exportSchema = true
)
abstract class AnankeDatabase : RoomDatabase() {
    abstract fun gameDao(): GameDao
}
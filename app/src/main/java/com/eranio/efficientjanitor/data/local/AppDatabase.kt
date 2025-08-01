package com.eranio.efficientjanitor.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [BagEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun bagDao(): BagDao
}
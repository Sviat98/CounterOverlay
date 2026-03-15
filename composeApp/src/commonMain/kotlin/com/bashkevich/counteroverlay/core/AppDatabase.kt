package com.bashkevich.counteroverlay.core

import androidx.room3.ConstructedBy
import androidx.room3.Database
import androidx.room3.RoomDatabase
import androidx.room3.RoomDatabaseConstructor
import com.bashkevich.counteroverlay.counter.local.room.CounterEntity
import com.bashkevich.counteroverlay.counter.local.room.CounterDao

@Database(
    entities = [CounterEntity::class],
    version = 1
)

@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun counterDao(): CounterDao
}

// Room compiler will generate actual implementations
@Suppress("KotlinNoActualForExpect")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}

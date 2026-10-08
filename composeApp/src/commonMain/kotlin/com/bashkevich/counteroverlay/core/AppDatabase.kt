package com.bashkevich.counteroverlay.core

import androidx.room3.ConstructedBy
import androidx.room3.Database
import androidx.room3.RoomDatabase
import androidx.room3.RoomDatabaseConstructor
import com.bashkevich.counteroverlay.counter.local.room.CounterEntity
import com.bashkevich.counteroverlay.counter.local.room.CounterDao
import com.bashkevich.counteroverlay.theme.local.room.ThemeEntity
import com.bashkevich.counteroverlay.theme.local.room.ThemeDao

@Database(
    entities = [CounterEntity::class, ThemeEntity::class],
    version = 1
)

@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun counterDao(): CounterDao
    abstract fun themeDao(): ThemeDao
}

// Room compiler will generate actual implementations
@Suppress("KotlinNoActualForExpect")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}

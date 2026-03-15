package com.bashkevich.counteroverlay.core

import androidx.room3.Room
import androidx.room3.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver

actual fun getDatabaseBuilder(
    platformConfiguration: PlatformConfiguration
): RoomDatabase.Builder<AppDatabase> {
    val context = platformConfiguration.context
    val appContext = context.applicationContext
    val dbFile = appContext.getDatabasePath("counter_room.db")

    return Room.databaseBuilder<AppDatabase>(
        context = appContext,
        name = dbFile.absolutePath
    ).setDriver(BundledSQLiteDriver())
}

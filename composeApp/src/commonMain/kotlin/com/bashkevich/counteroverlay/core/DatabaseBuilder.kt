package com.bashkevich.counteroverlay.core

import androidx.room3.RoomDatabase

/**
 * Platform-dependent function to create Room database builder.
 * Uses PlatformConfiguration to get platform context.
 */
expect fun getDatabaseBuilder(
    platformConfiguration: PlatformConfiguration
): RoomDatabase.Builder<AppDatabase>

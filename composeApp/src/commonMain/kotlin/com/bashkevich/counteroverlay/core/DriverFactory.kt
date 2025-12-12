package com.bashkevich.counteroverlay.core

import app.cash.sqldelight.db.SqlDriver

expect class DriverFactory(platformConfiguration: PlatformConfiguration) {
    fun createDriver(): SqlDriver
}

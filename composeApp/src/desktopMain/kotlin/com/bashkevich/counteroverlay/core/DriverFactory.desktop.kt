package com.bashkevich.counteroverlay.core

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.bashkevich.counteroverlay.CounterDatabase
import java.io.File

actual class DriverFactory actual constructor(platformConfiguration: PlatformConfiguration) {
    actual fun createDriver(): SqlDriver {
        val driver: SqlDriver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        return driver
    }
}

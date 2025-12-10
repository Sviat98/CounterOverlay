package com.bashkevich.counteroverlay.core

import app.cash.sqldelight.async.coroutines.synchronous
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.bashkevich.counteroverlay.CounterDatabase

actual class DriverFactory actual constructor(private val platformConfiguration: PlatformConfiguration) {
    actual fun createDriver(): SqlDriver {
        val driver: SqlDriver = AndroidSqliteDriver(CounterDatabase.Schema.synchronous(), platformConfiguration.context, "test.db")
        //CounterDatabase.Schema.create(driver)
        return driver
    }
}

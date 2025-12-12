package com.bashkevich.counteroverlay.core

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.worker.WebWorkerDriver
import org.w3c.dom.Worker

actual class DriverFactory actual constructor(platformConfiguration: PlatformConfiguration) {
    actual fun createDriver(): SqlDriver {
        val driver = WebWorkerDriver(
            worker = webWorker()
        )
        return driver
    }
}

@OptIn(ExperimentalWasmJsInterop::class)
fun webWorker(): Worker =js("""new Worker(new URL("@cashapp/sqldelight-sqljs-worker/sqljs.worker.js", import.meta.url))""")



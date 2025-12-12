package com.bashkevich.counteroverlay.di

import app.cash.sqldelight.async.coroutines.awaitCreate
import com.bashkevich.counteroverlay.CounterDatabase
import com.bashkevich.counteroverlay.core.BASE_URL_REMOTE_BACKEND
import com.bashkevich.counteroverlay.core.DriverFactory
import com.bashkevich.counteroverlay.core.PlatformConfiguration
import com.bashkevich.counteroverlay.core.httpClient
import com.bashkevich.counteroverlay.counter.local.CounterLocalDataSource
import com.bashkevich.counteroverlay.counter.remote.CounterRemoteDataSource
import com.bashkevich.counteroverlay.counter.repository.CounterRepository
import com.bashkevich.counteroverlay.counter.repository.CounterRepositoryImpl
import com.bashkevich.counteroverlay.screens.addcounterdialog.AddCounterDialogViewModel
import com.bashkevich.counteroverlay.screens.counterdetails.CounterDetailsViewModel
import com.bashkevich.counteroverlay.screens.counterlist.CounterListViewModel
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.http.ContentType
import io.ktor.http.URLProtocol
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.KotlinxWebsocketSerializationConverter
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val counterModule = module {
    viewModelOf(::CounterListViewModel)
    viewModelOf(::AddCounterDialogViewModel)
    viewModelOf(::CounterDetailsViewModel)

    singleOf(::CounterRepositoryImpl) {
        bind<CounterRepository>()
    }
    singleOf(::CounterRemoteDataSource)
    singleOf(::CounterLocalDataSource)
}

val coreModule = module {
    val jsonSerializer = Json {
        prettyPrint = true
        isLenient = true
    }

    single {
        httpClient {
            defaultRequest {
                url {
//                    protocol = URLProtocol.HTTP
//                    host = BASE_URL_LOCAL_BACKEND
                    protocol = URLProtocol.HTTPS
                    host = BASE_URL_REMOTE_BACKEND
                }
                contentType(ContentType.Application.Json)
            }
            install(Logging) {
                level = LogLevel.ALL
            }
            install(ContentNegotiation) {
                json(jsonSerializer)
            }
            install(WebSockets) {
                pingIntervalMillis = 20_000
                contentConverter = KotlinxWebsocketSerializationConverter(Json)
            }
        }
    }

    single {
        val platformConfiguration = get<PlatformConfiguration>()

        val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

        val driver = DriverFactory(platformConfiguration).createDriver().also {
            scope.launch {
                CounterDatabase.Schema.awaitCreate(it)
            }.invokeOnCompletion {
                // После завершения корутины отменяем scope
                scope.cancel()
            }
        }
        CounterDatabase(driver)
    }
}

expect val platformModule: Module

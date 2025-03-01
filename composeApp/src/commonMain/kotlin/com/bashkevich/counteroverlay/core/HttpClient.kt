package com.bashkevich.counteroverlay.core

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig

const val BASE_URL_FRONTEND = "http://localhost:8081/"

const val BASE_URL_LOCAL_BACKEND = "localhost:8080/"

expect fun httpClient(config: HttpClientConfig<*>.() -> Unit = {}): HttpClient
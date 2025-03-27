package com.bashkevich.counteroverlay.core

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig

const val BASE_URL_FRONTEND = "https://counteroverlay.onrender.com/"

const val BASE_URL_LOCAL_BACKEND = "http://localhost:8080/"

const val BASE_URL_REMOTE_BACKEND = "counteroverlaybackend.onrender.com"

expect fun httpClient(config: HttpClientConfig<*>.() -> Unit = {}): HttpClient
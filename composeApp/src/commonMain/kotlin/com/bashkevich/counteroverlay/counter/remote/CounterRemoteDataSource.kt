package com.bashkevich.counteroverlay.counter.remote

import com.bashkevich.counteroverlay.core.BASE_URL_LOCAL_BACKEND
import com.bashkevich.counteroverlay.core.LoadResult
import com.bashkevich.counteroverlay.core.ResponseMessage
import com.bashkevich.counteroverlay.core.runOperationCatching
import com.bashkevich.counteroverlay.core.webSocketDispatcher
import com.bashkevich.counteroverlay.counter.remote.CounterDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.plugins.websocket.receiveDeserialized
import io.ktor.client.plugins.websocket.webSocketSession
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.URLProtocol
import io.ktor.http.path
import io.ktor.websocket.Frame
import io.ktor.websocket.close
import io.ktor.websocket.readReason
import io.ktor.websocket.readText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json


class CounterRemoteDataSource(
    private val httpClient: HttpClient
) {
    private var webSocketSession: DefaultClientWebSocketSession? = null
    private val scope = CoroutineScope(SupervisorJob() + webSocketDispatcher)


    private val _counterFlow = MutableSharedFlow<LoadResult<CounterDto, Throwable>>(
        replay = 1, // Always emit the latest counter update
        extraBufferCapacity = 5
    )

    suspend fun getCounters(): LoadResult<List<CounterDto>, Throwable> {
        return runOperationCatching {
            val counters = httpClient.get("/counters").body<List<CounterDto>>()

            counters
        }
    }

    suspend fun addCounter(
        counterBody: AddCounterBody,
    ): LoadResult<CounterDto, Throwable> {
        return runOperationCatching {
            val counterDto = httpClient.post("/counters") {
                setBody(counterBody)
            }.body<CounterDto>()

            println(counterDto)
            counterDto
        }
    }

    suspend fun updateCounterValue(
        counterId: String,
        counterDeltaDto: CounterDeltaDto
    ): LoadResult<ResponseMessage, Throwable> {
        return runOperationCatching {
            val message = httpClient.patch("/counters/$counterId") {
                setBody(counterDeltaDto)
            }.body<ResponseMessage>()

            println(message)
            message
        }
    }

    fun observeCounterUpdates(): SharedFlow<LoadResult<CounterDto, Throwable>> =
        _counterFlow.asSharedFlow() // Expose as read-only flow

    fun connectToCounterUpdates(counterId: String) {
        var reconnectionTime = 5000L
        scope.launch {
            while (true) {
                try {

                    webSocketSession =
                        httpClient.webSocketSession {
                            url {
                                protocol = URLProtocol.WSS
                                host = BASE_URL_LOCAL_BACKEND
                                port = 443
                                path("/counters/$counterId")
                            }
                        }

                    println("Connected to WebSocket")
                    innerloop@ while (true) { // Внутренний цикл для чтения сообщений
                        try {
                            for (frame in webSocketSession!!.incoming) {
                                when (frame) {
                                    is Frame.Text -> {
                                        println(frame.readText())
                                        val counterDto = Json.decodeFromString<CounterDto>(frame.readText())
                                        _counterFlow.emit(LoadResult.Success(counterDto))
                                    }
                                    is Frame.Close -> {
                                        println("Connection closed: ${frame.readReason()}")
                                        webSocketSession?.close()
                                        break@innerloop
                                    }
                                    else -> Unit
                                }
                            }
                        } catch (e: Exception) {
                            println("Error reading frame: ${e.message}")
                            _counterFlow.emit(LoadResult.Error(e))
                            reconnectionTime = 1000L
                        }
                    }
                } catch (e: Exception) {
                    _counterFlow.emit(LoadResult.Error(e))
                }
                delay(reconnectionTime)
            }
        }
    }

    suspend fun closeSession() {
        scope.cancel()
        webSocketSession?.close()
    }
}
package com.bashkevich.counteroverlay.counter.remote

import com.bashkevich.counteroverlay.core.LoadResult
import com.bashkevich.counteroverlay.core.runOperationCatching
import com.bashkevich.counteroverlay.core.webSocketDispatcher
import com.bashkevich.counteroverlay.counter.remote.CounterDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.plugins.websocket.receiveDeserialized
import io.ktor.client.plugins.websocket.webSocketSession
import io.ktor.client.request.get
import io.ktor.websocket.close
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch


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

    fun observeCounterUpdates(): SharedFlow<LoadResult<CounterDto, Throwable>> =
        _counterFlow.asSharedFlow() // Expose as read-only flow

    fun connectToCounterUpdates(counterId: String) {
        scope.launch {
            while (true) {
                try {
                    webSocketSession =
//                        httpClient.webSocketSession(method = HttpMethod.Get, host =  "tennisscorekeeperbackend.onrender.com", path = "/counters/$counterId"){
//                            url { protocol = URLProtocol.WSS}
//                        }

                    httpClient.webSocketSession(urlString =  "wss://tennisscorekeeperbackend.onrender.com/counters/$counterId")

                    val counterDto = webSocketSession!!.receiveDeserialized<CounterDto>()
                    _counterFlow.emit(LoadResult.Success(counterDto))


//                    for (frame in webSocketSession!!.incoming) {
//                        if (frame is Frame.Text) {
//                            println(frame.readText())
////                            val counterDto = Json.decodeFromString<CounterDto>(frame.readText())
//                            val counterDto = webSocketSession!!.receiveDeserialized<CounterDto>()
//                            _counterFlow.emit(LoadResult.Success(counterDto))
//                        }
//                    }
                } catch (e: Exception) {
                    _counterFlow.emit(LoadResult.Error(e))
                    delay(5000) // Wait before reconnecting
                }
            }
        }
    }

    suspend fun closeSession() {
        webSocketSession?.close()
    }
}
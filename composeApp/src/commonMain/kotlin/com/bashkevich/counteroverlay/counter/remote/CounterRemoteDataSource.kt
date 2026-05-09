package com.bashkevich.counteroverlay.counter.remote

import com.bashkevich.counteroverlay.core.LoadResult
import com.bashkevich.counteroverlay.core.ResponseMessage
import com.bashkevich.counteroverlay.core.runOperationCatching
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.path


class CounterRemoteDataSource(
    private val httpClient: HttpClient
) {

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
            println("counterRemoteDataSource addCounter CALL ${counterBody.hashCode()}")
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

    suspend fun getCounterById(counterId: String): LoadResult<CounterDto, Throwable> {
        return runOperationCatching {
            val counter = httpClient.get {
                url.path("/counters", counterId)
            }.body<CounterDto>()

            counter
        }
    }
}
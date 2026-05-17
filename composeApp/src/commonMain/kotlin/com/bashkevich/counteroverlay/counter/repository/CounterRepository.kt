package com.bashkevich.counteroverlay.counter.repository

import com.bashkevich.counteroverlay.core.LoadResult
import com.bashkevich.counteroverlay.counter.Counter
import com.bashkevich.counteroverlay.counter.remote.AddCounterBody
import kotlinx.coroutines.flow.Flow

interface CounterRepository {
    suspend fun fetchCounters(): LoadResult<Unit, Throwable>
    suspend fun addCounter(addCounterBody: AddCounterBody): LoadResult<Unit, Throwable>
    suspend fun updateCounterValue(counterId: String, delta: Int)
    fun connectToCounterUpdates(counterId: String)
    fun observeCounterUpdatesFromWebSocket(): Flow<LoadResult<Unit, Throwable>>
    suspend fun closeSession()
    fun observeCountersFromDatabase(): Flow<List<Counter>>
    fun observeCounterByIdFromDatabase(counterId: String): Flow<Counter>
}
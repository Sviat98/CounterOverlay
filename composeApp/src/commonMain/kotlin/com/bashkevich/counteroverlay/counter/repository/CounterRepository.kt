package com.bashkevich.counteroverlay.counter.repository

import com.bashkevich.counteroverlay.core.LoadResult
import com.bashkevich.counteroverlay.counter.Counter
import com.bashkevich.counteroverlay.counter.remote.AddCounterBody
import kotlinx.coroutines.flow.Flow

interface CounterRepository {
    suspend fun fetchCounters(): LoadResult<Unit, Throwable>
    suspend fun observeCounters(): Flow<List<Counter>>
    suspend fun observeCounterById(counterId: String): Flow<Counter>
    suspend fun closeSession()
    fun connectToCounterUpdates(counterId: String)
    suspend fun updateCounterValue(counterId: String, delta: Int)
    suspend fun addCounter(addCounterBody: AddCounterBody): LoadResult<Unit, Throwable>
    suspend fun getCountersLocal(): LoadResult<List<Counter>, Throwable>
}
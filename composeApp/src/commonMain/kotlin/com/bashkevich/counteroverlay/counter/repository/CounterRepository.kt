package com.bashkevich.counteroverlay.counter.repository

import com.bashkevich.counteroverlay.core.LoadResult
import com.bashkevich.counteroverlay.counter.Counter
import kotlinx.coroutines.flow.Flow

interface CounterRepository {
    suspend fun getCounters(): LoadResult<List<Counter>, Throwable>
    suspend fun closeSession()
    fun connectToCounterUpdates(counterId: String)
    fun observeCounterUpdates(): Flow<LoadResult<Counter, Throwable>>
    suspend fun updateCounterValue(counterId: String, delta: Int)
    suspend fun addCounter(counterName: String): LoadResult<Counter, Throwable>
}
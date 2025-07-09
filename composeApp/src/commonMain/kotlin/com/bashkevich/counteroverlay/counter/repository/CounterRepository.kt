package com.bashkevich.counteroverlay.counter.repository

import com.bashkevich.counteroverlay.core.LoadResult
import com.bashkevich.counteroverlay.counter.Counter
import com.bashkevich.counteroverlay.counter.remote.AddCounterBody
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow

interface CounterRepository {
    suspend fun getCounters(): LoadResult<List<Counter>, Throwable>
    suspend fun closeSession()
    fun connectToCounterUpdates(counterId: String)
    fun observeCounterUpdates(): Flow<LoadResult<Counter, Throwable>>
    suspend fun updateCounterValue(counterId: String, delta: Int)
    suspend fun addCounter(addCounterBody: AddCounterBody): LoadResult<Counter, Throwable>
    fun emitNewCounter(addCounterBody: AddCounterBody)
    fun observeNewCounter(): SharedFlow<AddCounterBody>
    suspend fun getCountersLocal(): LoadResult<List<Counter>, Throwable>
}
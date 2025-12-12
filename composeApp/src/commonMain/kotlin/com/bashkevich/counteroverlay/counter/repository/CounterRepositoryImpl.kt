package com.bashkevich.counteroverlay.counter.repository

import com.bashkevich.counteroverlay.core.LoadResult
import com.bashkevich.counteroverlay.core.doOnSuccess
import com.bashkevich.counteroverlay.core.mapSuccess
import com.bashkevich.counteroverlay.counter.COUNTERS
import com.bashkevich.counteroverlay.counter.Counter
import com.bashkevich.counteroverlay.counter.local.CounterLocalDataSource
import com.bashkevich.counteroverlay.counter.remote.AddCounterBody
import com.bashkevich.counteroverlay.counter.remote.CounterDeltaDto
import com.bashkevich.counteroverlay.counter.remote.CounterRemoteDataSource
import com.bashkevich.counteroverlay.counter.remote.toEntity
import com.bashkevich.counteroverlay.counter.toDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class CounterRepositoryImpl(
    private val counterRemoteDataSource: CounterRemoteDataSource,
    private val counterLocalDataSource: CounterLocalDataSource
) : CounterRepository {
    override suspend fun fetchCounters(): LoadResult<Unit, Throwable> {
        return counterRemoteDataSource.getCounters().doOnSuccess { counterDtos ->
            val entities = counterDtos.map { it.toEntity() }
            counterLocalDataSource.replaceAllCounters(entities)
        }.mapSuccess {  }
    }

    override suspend fun observeCounters(): Flow<List<Counter>> {
        return counterLocalDataSource.getCounters().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun observeCounterById(counterId: String): Flow<Counter> {
        return counterLocalDataSource.getCounterById(counterId).map { it.toDomain() }
    }

    override suspend fun getCountersLocal(): LoadResult<List<Counter>, Throwable> {
        return LoadResult.Success(COUNTERS)
    }

    override suspend fun addCounter(addCounterBody: AddCounterBody): LoadResult<Unit, Throwable> {
        return counterRemoteDataSource.addCounter(addCounterBody).doOnSuccess { counterDto ->
            counterLocalDataSource.insertCounter(counterDto.toEntity())
        }.mapSuccess { }
    }

    override suspend fun updateCounterValue(counterId: String,delta: Int){
        val counterDeltaDto = CounterDeltaDto(delta)

        counterRemoteDataSource.updateCounterValue(counterId,counterDeltaDto).doOnSuccess {
            val counter = counterLocalDataSource.getCounterById(counterId).first()
            val amount = counter.amount + counterDeltaDto.delta.toLong()

            counterLocalDataSource.updateCounterValue(counterId,amount)
        }
    }

    override suspend fun closeSession() {
        counterRemoteDataSource.closeSession()
    }

    override fun connectToCounterUpdates(counterId: String) {
        counterRemoteDataSource.connectToCounterUpdates(counterId)
    }
}
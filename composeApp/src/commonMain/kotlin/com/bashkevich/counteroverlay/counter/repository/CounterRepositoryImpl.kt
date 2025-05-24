package com.bashkevich.counteroverlay.counter.repository

import com.bashkevich.counteroverlay.core.LoadResult
import com.bashkevich.counteroverlay.core.mapSuccess
import com.bashkevich.counteroverlay.counter.Counter
import com.bashkevich.counteroverlay.counter.remote.AddCounterBody
import com.bashkevich.counteroverlay.counter.remote.CounterDeltaDto
import com.bashkevich.counteroverlay.counter.remote.CounterRemoteDataSource
import com.bashkevich.counteroverlay.counter.toDomain
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map

class CounterRepositoryImpl(
    private val counterRemoteDataSource: CounterRemoteDataSource
) : CounterRepository {


    private val _newCounter = MutableSharedFlow<AddCounterBody>(replay = 1)

    override suspend fun getCounters(): LoadResult<List<Counter>, Throwable> {
        return counterRemoteDataSource.getCounters().mapSuccess { counterDtos ->
            val counters = counterDtos.map { it.toDomain() }
            counters
        }
    }

    override suspend fun addCounter(addCounterBody: AddCounterBody): LoadResult<Counter, Throwable> {
        println("counterRepository addCounter CALL ${addCounterBody.hashCode()}")

        return counterRemoteDataSource.addCounter(addCounterBody).mapSuccess { counterDto ->
            counterDto.toDomain()
        }
    }

    override suspend fun updateCounterValue(counterId: String,delta: Int){
        val counterDeltaDto = CounterDeltaDto(delta)

        counterRemoteDataSource.updateCounterValue(counterId,counterDeltaDto)
    }

    override suspend fun closeSession() {
        counterRemoteDataSource.closeSession()
    }

    override fun connectToCounterUpdates(counterId: String) {
        counterRemoteDataSource.connectToCounterUpdates(counterId)
    }

    override fun observeCounterUpdates() =
        counterRemoteDataSource.observeCounterUpdates()
            .map { result -> result.mapSuccess { counterDto -> counterDto.toDomain() } }

    override fun emitNewCounter(addCounterBody: AddCounterBody) {
        print("Emit counter ${addCounterBody.hashCode()}")
        _newCounter.tryEmit(addCounterBody)
    }

    override fun observeNewCounter() = _newCounter.asSharedFlow()
}
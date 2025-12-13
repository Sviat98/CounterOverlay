package com.bashkevich.counteroverlay.counter.local

import app.cash.sqldelight.async.coroutines.awaitAsOne
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOne
import app.cash.sqldelight.coroutines.mapToOneOrDefault
import com.bashkevich.counteroverlay.CounterDatabase
import com.bashkevich.counteroverlay.CounterEntity
import com.bashkevich.counteroverlay.core.backgroundDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class CounterLocalDataSource(
    private val db: CounterDatabase
) {
    private val queries = db.counterQueries

    private val DEFAULT_COUNTER_ENTITY = CounterEntity("0","Default",-1L)

    fun getCounters(): Flow<List<CounterEntity>> {
        return queries.getAllCounters()
            .asFlow()
            .mapToList(backgroundDispatcher)
    }

    fun getCounterById(id: String): Flow<CounterEntity> {
        return queries.getCounterById(id)
            .asFlow()
            .mapToOneOrDefault(defaultValue = DEFAULT_COUNTER_ENTITY, context = backgroundDispatcher)
    }

    suspend fun insertCounter(counter: CounterEntity) {
        queries.insertCounter(counter.id, counter.name, counter.amount)
    }
    
    suspend fun replaceAllCounters(counters: List<CounterEntity>) {
        queries.transaction {
            queries.deleteAllCounters()
            counters.forEach {
                queries.insertCounter(it.id, it.name, it.amount)
            }
        }
    }
}

package com.bashkevich.counteroverlay.counter.local

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.bashkevich.counteroverlay.CounterDatabase
import com.bashkevich.counteroverlay.CounterEntity
import com.bashkevich.counteroverlay.core.backgroundDispatcher
import kotlinx.coroutines.flow.Flow

class CounterLocalDataSource(
    private val db: CounterDatabase
) {
    private val queries = db.counterQueries

    fun getCounters(): Flow<List<CounterEntity>> {
        return queries.getAllCounters()
            .asFlow()
            .mapToList(backgroundDispatcher)
    }

    suspend fun insertCounter(counter: CounterEntity) {
        queries.insertCounter(counter.id, counter.name, counter.amount)
    }

    suspend fun deleteAllCounters() {
        queries.deleteAllCounters()
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

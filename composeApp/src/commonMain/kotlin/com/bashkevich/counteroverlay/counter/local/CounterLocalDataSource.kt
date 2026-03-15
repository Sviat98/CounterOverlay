package com.bashkevich.counteroverlay.counter.local

import com.bashkevich.counteroverlay.core.AppDatabase
import com.bashkevich.counteroverlay.counter.local.room.CounterDao
import com.bashkevich.counteroverlay.counter.local.room.CounterEntity as RoomCounterEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CounterLocalDataSource(
    private val db: AppDatabase
) {
    private val dao: CounterDao = db.counterDao()

    private val DEFAULT_COUNTER = RoomCounterEntity("0", "Default", -1)

    fun getCounters(): Flow<List<RoomCounterEntity>> {
        return dao.getAllCounters()
    }

    fun getCounterById(id: String): Flow<RoomCounterEntity> {
        return dao.getCounterById(id).map { it ?: DEFAULT_COUNTER }
    }

    suspend fun insertCounter(counter: RoomCounterEntity) {
        dao.insertCounter(counter)
    }

    suspend fun replaceAllCounters(counters: List<RoomCounterEntity>) {
        dao.deleteAllCounters()
        dao.insertCounters(counters)
    }
}

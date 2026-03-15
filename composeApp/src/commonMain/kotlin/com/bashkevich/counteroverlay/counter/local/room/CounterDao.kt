package com.bashkevich.counteroverlay.counter.local.room

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CounterDao {
    @Query("SELECT * FROM counters")
    fun getAllCounters(): Flow<List<CounterEntity>>

    @Query("SELECT * FROM counters WHERE id = :id")
    fun getCounterById(id: String): Flow<CounterEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCounter(counter: CounterEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCounters(counters: List<CounterEntity>)

    @Query("DELETE FROM counters")
    suspend fun deleteAllCounters()
}

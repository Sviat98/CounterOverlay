package com.bashkevich.counteroverlay.counter.local.room

import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = "counters")
data class CounterEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val amount: Int
)

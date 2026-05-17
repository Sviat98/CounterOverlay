package com.bashkevich.counteroverlay.counter

import com.bashkevich.counteroverlay.counter.local.room.CounterEntity
import com.bashkevich.counteroverlay.counter.remote.CounterDto

data class Counter(
    val id: String,
    val name: String,
    val value: Int,
    val themeId: String,
)

val COUNTER_DEFAULT = Counter("0","Counter 0",-1, "")

val COUNTERS = listOf(
    Counter(id = "1", name = "Counter 1", value = 1, themeId = ""),
    Counter(id = "2", name = "Counter 2", value = 2, themeId = ""),
    Counter(id = "3", name = "Counter 3", value = 3, themeId = "")
)

fun CounterDto.toDomain() = Counter(id, name, value, themeId)

fun CounterEntity.toDomain() = Counter(
    id = id,
    name = name,
    value = amount,
    themeId = themeId
)
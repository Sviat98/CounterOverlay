package com.bashkevich.counteroverlay.counter.remote

import com.bashkevich.counteroverlay.counter.local.room.CounterEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CounterDto(
    @SerialName(value = "id")
    val id: String,
    @SerialName(value = "name")
    val name: String,
    @SerialName(value = "value")
    val value: Int,
    @SerialName(value = "theme_id")
    val themeId: String,
)

@Serializable
data class AddCounterBody(
    @SerialName(value = "name")
    val name: String,
    @SerialName(value = "theme_id")
    val themeId: String,
)


@Serializable
data class CounterDeltaDto(
    @SerialName(value = "delta")
    val delta: Int,
)

fun CounterDto.toEntity() = CounterEntity(
    id = id,
    name = name,
    amount = value,
    themeId = themeId
)

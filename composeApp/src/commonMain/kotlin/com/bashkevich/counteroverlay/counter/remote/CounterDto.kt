package com.bashkevich.counteroverlay.counter.remote

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
)

@Serializable
data class AddCounterBody(
    @SerialName(value = "name")
    val name: String
)


@Serializable
data class CounterDeltaDto(
    @SerialName(value = "delta")
    val delta: Int,
)


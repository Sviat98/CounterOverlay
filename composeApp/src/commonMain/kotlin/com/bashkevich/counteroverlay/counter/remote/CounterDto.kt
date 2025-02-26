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

package com.bashkevich.counteroverlay.core

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponseMessage(
    @SerialName(value = "message")
    val message: String
)

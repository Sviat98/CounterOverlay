package com.bashkevich.counteroverlay.navigation

import androidx.navigation.NavGraphBuilder
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("counters")
data object CounterListRoute

@Serializable
@SerialName("counters/add")
data object AddCounterDialogRoute

@Serializable
@SerialName("counters")
data class CounterDetailsRoute(val id: String)

expect fun NavGraphBuilder.platformSpecificRoutes()
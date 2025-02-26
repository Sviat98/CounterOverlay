package com.bashkevich.counteroverlay.navigation

import androidx.navigation.NavGraphBuilder
import kotlinx.serialization.Serializable

@Serializable
data object CounterListRoute

@Serializable
data object AddCounterDialogRoute

@Serializable
data class CounterDetailsRoute(val id: String)

expect fun NavGraphBuilder.platformSpecificRoutes()
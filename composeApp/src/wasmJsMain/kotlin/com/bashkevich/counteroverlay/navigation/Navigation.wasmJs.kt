package com.bashkevich.counteroverlay.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.bashkevich.counteroverlay.screens.counteroverlay.CounterOverlayScreen
import com.bashkevich.counteroverlay.screens.counteroverlay.CounterOverlayViewModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel

@Serializable
@SerialName("counterOverlay")
data class CounterOverlayRoute(
    val counterId: String = ""
)

actual fun NavGraphBuilder.platformSpecificRoutes(){
    composable<CounterOverlayRoute>{
        val counterOverlayViewModel = koinViewModel<CounterOverlayViewModel>()
        CounterOverlayScreen(viewModel = counterOverlayViewModel)
    }
}


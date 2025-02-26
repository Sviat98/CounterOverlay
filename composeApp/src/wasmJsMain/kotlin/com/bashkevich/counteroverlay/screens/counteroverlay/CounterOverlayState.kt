package com.bashkevich.counteroverlay.screens.counteroverlay

import androidx.compose.runtime.Immutable
import com.bashkevich.counteroverlay.counter.COUNTER_DEFAULT
import com.bashkevich.counteroverlay.counter.Counter
import com.bashkevich.counteroverlay.mvi.UiAction
import com.bashkevich.counteroverlay.mvi.UiEvent
import com.bashkevich.counteroverlay.mvi.UiState

@Immutable
sealed class CounterOverlayUiEvent : UiEvent {
    class ShowCounter(val counter: Counter): CounterOverlayUiEvent()
}

@Immutable
data class CounterOverlayState(
    val counter: Counter
) : UiState {
    companion object {
        fun initial() = CounterOverlayState(
            counter = COUNTER_DEFAULT
        )
    }
}

@Immutable
sealed class CounterOverlayAction : UiAction {

}
package com.bashkevich.counteroverlay.screens.counterdetails

import androidx.compose.runtime.Immutable
import com.bashkevich.counteroverlay.counter.COUNTER_DEFAULT
import com.bashkevich.counteroverlay.counter.Counter

import com.bashkevich.counteroverlay.mvi.UiAction
import com.bashkevich.counteroverlay.mvi.UiEvent
import com.bashkevich.counteroverlay.mvi.UiState

@Immutable
sealed class CounterDetailsUiEvent : UiEvent {
    class ShowCounter(val counter: Counter): CounterDetailsUiEvent()
    class ChangeCounterValue(val counterId: String,val delta: Int): CounterDetailsUiEvent()
}

@Immutable
data class CounterDetailsState(
    val counter: Counter
) : UiState {
    companion object {
        fun initial() = CounterDetailsState(
            counter = COUNTER_DEFAULT
        )
    }
}

@Immutable
sealed class CounterDetailsAction : UiAction {

}
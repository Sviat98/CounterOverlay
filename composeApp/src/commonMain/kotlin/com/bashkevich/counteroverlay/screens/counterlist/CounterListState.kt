package com.bashkevich.counteroverlay.screens.counterlist

import androidx.compose.runtime.Immutable
import com.bashkevich.counteroverlay.counter.Counter

import com.bashkevich.counteroverlay.mvi.UiAction
import com.bashkevich.counteroverlay.mvi.UiEvent
import com.bashkevich.counteroverlay.mvi.UiState

@Immutable
sealed class CounterListUiEvent : UiEvent {
    data class ShowCounters(val counters: List<Counter>): CounterListUiEvent()
}

@Immutable
data class CounterListState(
    val counters: List<Counter>
) : UiState {
    companion object {
        fun initial() = CounterListState(
            counters = emptyList()
        )
    }
}

@Immutable
sealed class CounterListAction : UiAction {

}
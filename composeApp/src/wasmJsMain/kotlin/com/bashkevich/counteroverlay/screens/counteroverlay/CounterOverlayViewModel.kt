package com.bashkevich.counteroverlay.screens.counteroverlay

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.bashkevich.counteroverlay.core.LoadResult
import com.bashkevich.counteroverlay.counter.repository.CounterRepository
import com.bashkevich.counteroverlay.mvi.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.Flow

import com.bashkevich.counteroverlay.navigation.CounterOverlayRoute
import com.bashkevich.counteroverlay.screens.counteroverlay.CounterOverlayAction
import com.bashkevich.counteroverlay.screens.counteroverlay.CounterOverlayState
import com.bashkevich.counteroverlay.screens.counteroverlay.CounterOverlayUiEvent
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

class CounterOverlayViewModel(
    savedStateHandle: SavedStateHandle,
    private val counterRepository: CounterRepository
) :
    BaseViewModel<CounterOverlayState, CounterOverlayUiEvent, CounterOverlayAction>() {

    private val _state = MutableStateFlow(CounterOverlayState.initial())
    override val state: StateFlow<CounterOverlayState>
        get() = _state.asStateFlow()

    val actions: Flow<CounterOverlayAction>
        get() = super.action

    init {
        val counterId = savedStateHandle.toRoute<CounterOverlayRoute>().counterId

        counterRepository.connectToCounterUpdates(counterId = counterId)

        viewModelScope.launch {
            counterRepository.observeCounterById(counterId).distinctUntilChanged()
                .collect { counter ->
                    onEvent(CounterOverlayUiEvent.ShowCounter(counter))
                }
        }
    }

    fun onEvent(uiEvent: CounterOverlayUiEvent) {
        when (uiEvent) {
            is CounterOverlayUiEvent.ShowCounter -> {
                reduceState { oldState ->
                    oldState.copy(counter = uiEvent.counter)
                }
            }
        }
    }

    private fun reduceState(reducer: (CounterOverlayState) -> CounterOverlayState) {
        _state.update(reducer)
    }

}
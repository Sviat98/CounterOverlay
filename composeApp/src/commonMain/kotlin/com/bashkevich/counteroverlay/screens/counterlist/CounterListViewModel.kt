package com.bashkevich.counteroverlay.screens.counterlist

import androidx.lifecycle.viewModelScope
import com.bashkevich.counteroverlay.counter.repository.CounterRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.Flow

import com.bashkevich.counteroverlay.mvi.BaseViewModel
import com.bashkevich.counteroverlay.theme.repository.ThemeRepository
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlin.time.Clock

class CounterListViewModel(
    private val counterRepository: CounterRepository,
    private val themeRepository: ThemeRepository
) :
    BaseViewModel<CounterListState, CounterListUiEvent, CounterListAction>() {

    private val _state = MutableStateFlow(CounterListState.initial())
    override val state: StateFlow<CounterListState>
        get() = _state.asStateFlow()

    val actions: Flow<CounterListAction>
        get() = super.action

    init {
        viewModelScope.launch {

            coroutineScope {
                launch {
                    println("Themes fetch start: ${Clock.System.now()}")
                    themeRepository.fetchThemes()
                    println("Themes fetch end: ${Clock.System.now()}")
                }
                launch {
                    println("Counters fetch start: ${Clock.System.now()}")
                    counterRepository.fetchCounters()
                    println("Counters fetch end: ${Clock.System.now()}")
                }
            }

            //handle loadResult = error
        }


        viewModelScope.launch {
            counterRepository.observeCountersFromDatabase().distinctUntilChanged().collect { counters ->
                onEvent(CounterListUiEvent.ShowCounters(counters = counters))
            }
        }

        viewModelScope.launch {
            themeRepository.observeThemesFromDatabase().distinctUntilChanged().collect { themes ->
                onEvent(CounterListUiEvent.ShowThemes(themes = themes.associateBy { it.id }))
            }
        }
    }

    fun onEvent(uiEvent: CounterListUiEvent) {
        when (uiEvent) {
            is CounterListUiEvent.ShowCounters -> {
                reduceState { oldState -> oldState.copy(counters = uiEvent.counters) }
            }
            is CounterListUiEvent.ShowThemes -> {
                reduceState { oldState -> oldState.copy(themes = uiEvent.themes) }
            }
        }
    }

    private fun reduceState(reducer: (CounterListState) -> CounterListState) {
        _state.update(reducer)
    }

}

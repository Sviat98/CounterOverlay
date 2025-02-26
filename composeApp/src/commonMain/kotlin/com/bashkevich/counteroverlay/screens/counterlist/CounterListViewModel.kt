package com.bashkevich.counteroverlay.screens.counterlist

import androidx.lifecycle.viewModelScope
import com.bashkevich.counteroverlay.core.LoadResult
import com.bashkevich.counteroverlay.counter.repository.CounterRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.Flow

import com.bashkevich.counteroverlay.mvi.BaseViewModel
import com.bashkevich.counteroverlay.screens.counterlist.CounterListAction
import com.bashkevich.counteroverlay.screens.counterlist.CounterListState
import com.bashkevich.counteroverlay.screens.counterlist.CounterListUiEvent
import kotlinx.coroutines.launch

class CounterListViewModel(
    private val counterRepository: CounterRepository
) :
    BaseViewModel<CounterListState, CounterListUiEvent, CounterListAction>() {

    private val _state = MutableStateFlow(CounterListState.initial())
    override val state: StateFlow<CounterListState>
        get() = _state.asStateFlow()

    val actions: Flow<CounterListAction>
        get() = super.action

    init {
        viewModelScope.launch {
            val loadResult = counterRepository.getCounters()

            println(loadResult)

            if (loadResult is LoadResult.Success){
                onEvent(CounterListUiEvent.ShowCounters(counters = loadResult.result))
            }
        }
    }

    fun onEvent(uiEvent: CounterListUiEvent) {
        when(uiEvent){
            is CounterListUiEvent.ShowCounters -> {
                reduceState { oldState-> oldState.copy(counters = uiEvent.counters) }
            }
        }
    }

    private fun reduceState(reducer: (CounterListState) -> CounterListState) {
        _state.update(reducer)
    }

}
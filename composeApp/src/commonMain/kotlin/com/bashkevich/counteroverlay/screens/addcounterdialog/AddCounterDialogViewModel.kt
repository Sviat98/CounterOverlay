package com.bashkevich.counteroverlay.screens.addcounterdialog

import androidx.lifecycle.viewModelScope
import com.bashkevich.counteroverlay.core.LoadResult
import com.bashkevich.counteroverlay.counter.remote.AddCounterBody
import com.bashkevich.counteroverlay.counter.repository.CounterRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.Flow

import com.bashkevich.counteroverlay.mvi.BaseViewModel
import kotlinx.coroutines.launch

class AddCounterDialogViewModel(
    private val counterRepository: CounterRepository
) :
    BaseViewModel<AddCounterDialogState, AddCounterDialogUiEvent, AddCounterDialogAction>() {

    private val _state = MutableStateFlow(AddCounterDialogState.initial())
    override val state: StateFlow<AddCounterDialogState>
        get() = _state.asStateFlow()

    val actions: Flow<AddCounterDialogAction>
        get() = super.action

    fun onEvent(uiEvent: AddCounterDialogUiEvent) {
        when (uiEvent) {
            is AddCounterDialogUiEvent.AddCounter -> addCounter(uiEvent.counterName)
        }
    }

    private fun addCounter(counterName: String) {
        viewModelScope.launch {
            reduceState { oldState -> oldState.copy(addCounterState = AddCounterState.Loading) }
            val addCounterBody = AddCounterBody(counterName)
            val addCounterResult = counterRepository.addCounter(addCounterBody)

            if (addCounterResult is LoadResult.Error) {
                val message = addCounterResult.result.message ?: ""
                reduceState { oldState ->
                    oldState.copy(
                        addCounterState = AddCounterState.Error(
                            message = message
                        )
                    )
                }
            }
        }
    }

    private fun reduceState(reducer: (AddCounterDialogState) -> AddCounterDialogState) {
        _state.update(reducer)
    }

}
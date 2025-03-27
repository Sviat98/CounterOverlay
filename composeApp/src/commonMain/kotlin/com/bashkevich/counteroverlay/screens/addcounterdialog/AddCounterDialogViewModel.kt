package com.bashkevich.counteroverlay.screens.addcounterdialog

import androidx.lifecycle.viewModelScope
import com.bashkevich.counteroverlay.counter.remote.AddCounterBody
import com.bashkevich.counteroverlay.counter.repository.CounterRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.Flow

import com.bashkevich.counteroverlay.mvi.BaseViewModel
import com.bashkevich.counteroverlay.screens.addcounterdialog.AddCounterDialogAction
import com.bashkevich.counteroverlay.screens.addcounterdialog.AddCounterDialogState
import com.bashkevich.counteroverlay.screens.addcounterdialog.AddCounterDialogUiEvent
import kotlinx.coroutines.async
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
            is AddCounterDialogUiEvent.AddCounter -> {
                val addCounterBody = AddCounterBody(uiEvent.counterName)
                counterRepository.emitNewCounter(addCounterBody)
            }
        }
    }

    private fun reduceState(reducer: (AddCounterDialogState) -> AddCounterDialogState) {
        _state.update(reducer)
    }

}
package com.bashkevich.counteroverlay.screens.addcounterdialog

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.Flow

import com.bashkevich.counteroverlay.mvi.BaseViewModel
import com.bashkevich.counteroverlay.screens.addcounterdialog.AddCounterDialogAction
import com.bashkevich.counteroverlay.screens.addcounterdialog.AddCounterDialogState
import com.bashkevich.counteroverlay.screens.addcounterdialog.AddCounterDialogUiEvent

class AddCounterDialogViewModel :
    BaseViewModel<AddCounterDialogState, AddCounterDialogUiEvent, AddCounterDialogAction>() {

    private val _state = MutableStateFlow(AddCounterDialogState.initial())
    override val state: StateFlow<AddCounterDialogState>
        get() = _state.asStateFlow()

    val actions: Flow<AddCounterDialogAction>
        get() = super.action

    fun onEvent(uiEvent: AddCounterDialogUiEvent) {
        // some feature-specific logic
    }

    private fun reduceState(reducer: (AddCounterDialogState) -> AddCounterDialogState) {
        _state.update(reducer)
    }

}
package com.bashkevich.counteroverlay.screens.addcounterdialog

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Immutable

import com.bashkevich.counteroverlay.mvi.UiAction
import com.bashkevich.counteroverlay.mvi.UiEvent
import com.bashkevich.counteroverlay.mvi.UiState

@Immutable
sealed class AddCounterDialogUiEvent : UiEvent {
    class AddCounter(val counterName: String) : AddCounterDialogUiEvent()
}

@Immutable
data class AddCounterDialogState(
    val addCounterState: AddCounterState,
    val counterName: TextFieldState,
) : UiState {
    companion object {
        fun initial() = AddCounterDialogState(
            addCounterState = AddCounterState.Idle,
            counterName = TextFieldState("")
        )
    }
}

@Immutable
sealed class AddCounterState {
    data object Idle : AddCounterState()
    data object Loading : AddCounterState()
    data object Success : AddCounterState()
    data class Error(val message: String) : AddCounterState()
}

@Immutable
sealed class AddCounterDialogAction : UiAction {

}
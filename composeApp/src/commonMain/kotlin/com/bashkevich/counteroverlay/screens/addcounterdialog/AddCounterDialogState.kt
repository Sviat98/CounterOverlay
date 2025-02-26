package com.bashkevich.counteroverlay.screens.addcounterdialog

import androidx.compose.runtime.Immutable

import com.bashkevich.counteroverlay.mvi.UiAction
import com.bashkevich.counteroverlay.mvi.UiEvent
import com.bashkevich.counteroverlay.mvi.UiState

@Immutable
sealed class AddCounterDialogUiEvent : UiEvent {
}

@Immutable
data class AddCounterDialogState(
    val counterName: String,
) : UiState {
    companion object {
        fun initial() = AddCounterDialogState(
            counterName = ""
        )
    }
}

@Immutable
sealed class AddCounterDialogAction : UiAction {

}
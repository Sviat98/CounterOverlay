package com.bashkevich.counteroverlay.screens.addcounterdialog

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Immutable

import com.bashkevich.counteroverlay.counter.Counter
import com.bashkevich.counteroverlay.mvi.UiAction
import com.bashkevich.counteroverlay.mvi.UiEvent
import com.bashkevich.counteroverlay.mvi.UiState
import com.bashkevich.counteroverlay.theme.CounterTheme

@Immutable
sealed class AddCounterDialogUiEvent : UiEvent {
    class AddCounter(val counterName: String) : AddCounterDialogUiEvent()
    class SelectTheme(val themeId: String) : AddCounterDialogUiEvent()
    class ThemesLoaded(val themes: List<CounterTheme>) : AddCounterDialogUiEvent()
}

@Immutable
data class AddCounterDialogState(
    val addCounterState: AddCounterState,
    val counterName: TextFieldState,
    val themes: List<CounterTheme>,
    val selectedThemeId: String,
) : UiState {
    companion object {
        fun initial() = AddCounterDialogState(
            addCounterState = AddCounterState.Idle,
            counterName = TextFieldState(""),
            themes = emptyList(),
            selectedThemeId = "-1",
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

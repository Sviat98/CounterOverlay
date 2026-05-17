package com.bashkevich.counteroverlay.screens.counterdetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.bashkevich.counteroverlay.counter.repository.CounterRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.Flow

import com.bashkevich.counteroverlay.mvi.BaseViewModel
import com.bashkevich.counteroverlay.navigation.CounterDetailsRoute
import com.bashkevich.counteroverlay.theme.repository.ThemeRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class CounterDetailsViewModel(
    savedStateHandle: SavedStateHandle,
    private val counterRepository: CounterRepository,
    private val themeRepository: ThemeRepository
) :
    BaseViewModel<CounterDetailsState, CounterDetailsUiEvent, CounterDetailsAction>() {

    private val _state = MutableStateFlow(CounterDetailsState.initial())
    override val state: StateFlow<CounterDetailsState>
        get() = _state.asStateFlow()

    val actions: Flow<CounterDetailsAction>
        get() = super.action

    init {
        val counterId = savedStateHandle.toRoute<CounterDetailsRoute>().id

        counterRepository.connectToCounterUpdates(counterId = counterId)

        viewModelScope.launch {
            counterRepository.observeCounterUpdatesFromWebSocket().collect { }
        }

        viewModelScope.launch {
            counterRepository.observeCounterByIdFromDatabase(counterId)
                .distinctUntilChanged()
                .onEach { counter ->
                    onEvent(CounterDetailsUiEvent.ShowCounter(counter))
                }
                .map { it.themeId }
                .distinctUntilChanged()
                .filter { it.isNotEmpty() }
                .onEach { themeId ->
                    themeRepository.fetchThemeById(themeId)
                }
                .flatMapLatest { themeId ->
                    themeRepository.observeThemeByIdFromDatabase(themeId)
                }
                .collect { theme ->
                    onEvent(CounterDetailsUiEvent.ShowTheme(theme))
                }
        }

    }

    fun onEvent(uiEvent: CounterDetailsUiEvent) {
        when (uiEvent) {
            is CounterDetailsUiEvent.ShowCounter -> {
                reduceState { oldState ->
                    oldState.copy(counter = uiEvent.counter)
                }
            }

            is CounterDetailsUiEvent.ShowTheme -> {
                reduceState { oldState ->
                    oldState.copy(theme = uiEvent.theme)
                }
            }

            is CounterDetailsUiEvent.ChangeCounterValue -> {
                viewModelScope.launch {
                    counterRepository.updateCounterValue(uiEvent.counterId, uiEvent.delta)
                }
            }
        }
    }

    private fun reduceState(reducer: (CounterDetailsState) -> CounterDetailsState) {
        _state.update(reducer)
    }

    override fun onCleared() {
        viewModelScope.launch {
            counterRepository.closeSession()
        }
        super.onCleared()
    }
}

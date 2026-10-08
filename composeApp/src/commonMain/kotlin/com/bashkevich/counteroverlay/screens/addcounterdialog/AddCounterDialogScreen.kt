package com.bashkevich.counteroverlay.screens.addcounterdialog

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bashkevich.counteroverlay.theme.CounterTheme
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AddCounterDialogScreen(
    modifier: Modifier = Modifier,
    viewModel: AddCounterDialogViewModel = koinViewModel(),
    onDismissRequest: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.actions.collect { action ->
        }
    }


    val buttonBackgroundColor = MaterialTheme.colors.primary

    AddCounterDialogContent(
        state = state,
        onEvent = { viewModel.onEvent(it) },
        onDismissRequest = onDismissRequest
    )
}

@Composable
fun AddCounterDialogContent(
    modifier: Modifier = Modifier,
    state: AddCounterDialogState,
    onEvent: (AddCounterDialogUiEvent) -> Unit,
    onDismissRequest: () -> Unit = {},
) {
    if (state.addCounterState is AddCounterState.Success) {
        onDismissRequest()
    }
    Column(
        modifier = Modifier.then(modifier).background(MaterialTheme.colors.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Add")
        val counterName = state.counterName

        TextField(
            state = counterName
        )

        Spacer(Modifier.size(8.dp))

        ThemeDropdownMenu(
            themes = state.themes,
            selectedThemeId = state.selectedThemeId,
            onThemeSelected = { themeId ->
                onEvent(AddCounterDialogUiEvent.SelectTheme(themeId))
            }
        )

        val addCounterState = state.addCounterState

        val enabled = addCounterState !is AddCounterState.Loading

        val scope = rememberCoroutineScope()
        Button(
            onClick = {
                scope.launch {
                    onEvent(AddCounterDialogUiEvent.AddCounter(counterName.text.toString()))
                }
            },
            enabled = enabled
        ) {
            if (addCounterState is AddCounterState.Loading) {
                CircularProgressIndicator()
            } else {
                Text("Add")
            }
        }
        Button(
            onClick = {
                onDismissRequest()
            },
        ) {
            Text("Back")
        }
    }
}

@Composable
private fun ThemeDropdownMenu(
    themes: List<CounterTheme>,
    selectedThemeId: String,
    onThemeSelected: (String) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedTheme = themes.find { it.id == selectedThemeId }

    Box {
        Row(
            modifier = Modifier
                .clip(MaterialTheme.shapes.medium)
                .border(1.dp, MaterialTheme.colors.onSurface.copy(alpha = 0.3f), MaterialTheme.shapes.medium)
                .clickable { expanded = true }
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (selectedTheme != null) {
                ColorCircle(selectedTheme.backgroundColor)
                Spacer(Modifier.width(4.dp))
                ColorCircle(selectedTheme.textColor)
                Spacer(Modifier.width(8.dp))
                Text("Тема ${selectedTheme.id}")
            } else {
                Text("Выберите тему")
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            themes.forEach { theme ->
                DropdownMenuItem(
                    onClick = {
                        onThemeSelected(theme.id)
                        expanded = false
                    }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        ColorCircle(theme.backgroundColor)
                        Spacer(Modifier.width(4.dp))
                        ColorCircle(theme.textColor)
                        Spacer(Modifier.width(8.dp))
                        Text("Тема ${theme.id}")
                    }
                }
            }
        }
    }
}

@Composable
private fun ColorCircle(
    color: Color,
) {
    Box(
        modifier = Modifier
            .size(16.dp)
            .clip(CircleShape)
            .background(color)
            .border(1.dp, Color.Gray, CircleShape)
    )
}

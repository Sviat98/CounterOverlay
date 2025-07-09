package com.bashkevich.counteroverlay.screens.addcounterdialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment

import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
        Button(
            onClick = {
                onEvent(AddCounterDialogUiEvent.AddCounter(counterName.text.toString()))
                onDismissRequest()
            },
        ) {
            Text("Add")
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
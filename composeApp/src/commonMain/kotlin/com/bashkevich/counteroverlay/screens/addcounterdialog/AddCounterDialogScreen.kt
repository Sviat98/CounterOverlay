package com.bashkevich.counteroverlay.screens.addcounterdialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bashkevich.counteroverlay.components.InteractiveButton
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AddCounterDialogScreen(
    modifier: Modifier = Modifier,
    viewModel: AddCounterDialogViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.actions.collect { action ->
        }
    }

    val counterName = rememberTextFieldState()

    val buttonBackgroundColor = MaterialTheme.colors.primary

    Column(
        modifier = Modifier.then(modifier).background(MaterialTheme.colors.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BasicTextField(
            state = counterName
        )
        InteractiveButton(
            onClick = {},
            onMouseHoveredButtonColor = Color.DarkGray,
        ) {
            Text("Add")
        }
    }
}
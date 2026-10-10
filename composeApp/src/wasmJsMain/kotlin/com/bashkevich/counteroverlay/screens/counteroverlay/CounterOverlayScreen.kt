package com.bashkevich.counteroverlay.screens.counteroverlay

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.viewinterop.HtmlElementView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.browser.document
import org.koin.compose.viewmodel.koinViewModel
import org.w3c.dom.HTMLElement

@Composable
fun CounterOverlayScreen(
    modifier: Modifier = Modifier,
    viewModel: CounterOverlayViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.actions.collect { action ->
        }
    }

    CounterOverlayContent(
        modifier = Modifier.then(modifier),
        state = state
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun CounterOverlayContent(
    modifier: Modifier = Modifier,
    state: CounterOverlayState,
) {
    Box(modifier = Modifier.then(modifier)
        .drawBehind {
            drawRect(
                color = Color.Transparent,
                size = this.size,
                blendMode = BlendMode.Clear
            )
        }
        .fillMaxSize()) {
        // HtmlElementView is measured to the incoming min constraints, so the element
        // must fill the box; the counter card itself is centered with CSS flex
        HtmlElementView(
            modifier = Modifier.fillMaxSize(),
            factory = { createCounterOverlayElement() },
            update = { container ->
                (container.firstElementChild as HTMLElement).textContent =
                    "Current value: ${state.counter.value}"
            }
        )
    }
}

private fun createCounterOverlayElement(): HTMLElement {
    val container = document.createElement("div") as HTMLElement
    container.setAttribute(
        "style",
        "display:flex;align-items:center;justify-content:center;"
    )
    val card = document.createElement("div") as HTMLElement
    card.setAttribute(
        "style",
        "background-color:#0000FF;color:#FFFFFF;padding:8px;font-size:18px;font-family:sans-serif;"
    )
    card.textContent = "Current value: -1"
    container.appendChild(card)
    return container
}
package com.bashkevich.counteroverlay.components

import androidx.compose.ui.platform.Clipboard


expect suspend fun Clipboard.setText(text: String)
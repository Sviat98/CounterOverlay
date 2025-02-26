package com.bashkevich.counteroverlay.core

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

internal actual val webSocketDispatcher: CoroutineDispatcher = Dispatchers.IO
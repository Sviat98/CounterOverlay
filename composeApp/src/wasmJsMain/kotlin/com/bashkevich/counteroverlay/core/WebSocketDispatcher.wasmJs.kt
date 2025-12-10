package com.bashkevich.counteroverlay.core

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

internal actual val backgroundDispatcher: CoroutineDispatcher = Dispatchers.Default
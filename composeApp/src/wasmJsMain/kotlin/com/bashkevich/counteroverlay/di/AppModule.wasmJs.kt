package com.bashkevich.counteroverlay.di

import com.bashkevich.counteroverlay.screens.counteroverlay.CounterOverlayViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

actual val platformModule = module {
    viewModelOf(::CounterOverlayViewModel)
}
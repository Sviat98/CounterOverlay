package com.bashkevich.counteroverlay.di

import com.bashkevich.counteroverlay.core.PlatformConfiguration
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

actual val platformModule = module {
    singleOf(::PlatformConfiguration)
}
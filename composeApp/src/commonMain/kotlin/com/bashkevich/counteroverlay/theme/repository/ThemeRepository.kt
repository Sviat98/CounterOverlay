package com.bashkevich.counteroverlay.theme.repository

import com.bashkevich.counteroverlay.core.LoadResult
import com.bashkevich.counteroverlay.theme.CounterTheme
import kotlinx.coroutines.flow.Flow

interface ThemeRepository {
    suspend fun fetchThemes(): LoadResult<Unit, Throwable>
    suspend fun fetchThemeById(id: String): LoadResult<Unit, Throwable>
    suspend fun observeThemesFromDatabase(): Flow<List<CounterTheme>>
    suspend fun observeThemeByIdFromDatabase(id: String): Flow<CounterTheme>
}

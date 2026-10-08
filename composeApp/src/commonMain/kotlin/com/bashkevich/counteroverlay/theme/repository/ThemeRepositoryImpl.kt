package com.bashkevich.counteroverlay.theme.repository

import com.bashkevich.counteroverlay.core.LoadResult
import com.bashkevich.counteroverlay.core.doOnSuccess
import com.bashkevich.counteroverlay.core.mapSuccess
import com.bashkevich.counteroverlay.theme.CounterTheme
import com.bashkevich.counteroverlay.theme.local.ThemeLocalDataSource
import com.bashkevich.counteroverlay.theme.remote.ThemeRemoteDataSource
import com.bashkevich.counteroverlay.theme.remote.toEntity
import com.bashkevich.counteroverlay.theme.toDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json

class ThemeRepositoryImpl(
    private val themeRemoteDataSource: ThemeRemoteDataSource,
    private val themeLocalDataSource: ThemeLocalDataSource,
) : ThemeRepository {

    override suspend fun fetchThemes(): LoadResult<Unit, Throwable> {
        return themeRemoteDataSource.getThemes().doOnSuccess { themeDtos ->
            val entities = themeDtos.map { it.toEntity() }
            themeLocalDataSource.replaceAllThemes(entities)
        }.mapSuccess { }
    }

    override suspend fun fetchThemeById(id: String): LoadResult<Unit, Throwable> {
        return themeRemoteDataSource.getThemeById(id).doOnSuccess { themeDto ->
            themeLocalDataSource.insertTheme(themeDto.toEntity())
        }.mapSuccess { }
    }

    override suspend fun observeThemesFromDatabase(): Flow<List<CounterTheme>> {
        return themeLocalDataSource.getThemes().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun observeThemeByIdFromDatabase(id: String): Flow<CounterTheme> {
        return themeLocalDataSource.getThemeById(id).map { entity ->
            entity?.toDomain() ?: CounterTheme.DEFAULT
        }
    }
}

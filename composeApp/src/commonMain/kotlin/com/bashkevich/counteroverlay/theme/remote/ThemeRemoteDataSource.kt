package com.bashkevich.counteroverlay.theme.remote

import com.bashkevich.counteroverlay.core.LoadResult
import com.bashkevich.counteroverlay.core.runOperationCatching
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class ThemeRemoteDataSource(
    private val httpClient: HttpClient
) {
    suspend fun getThemes(): LoadResult<List<ThemeDto>, Throwable> {
        return runOperationCatching {
            httpClient.get("/themes").body<List<ThemeDto>>()
        }
    }

    suspend fun getThemeById(id: String): LoadResult<ThemeDto, Throwable> {
        return runOperationCatching {
            httpClient.get("/themes/$id").body<ThemeDto>()
        }
    }
}

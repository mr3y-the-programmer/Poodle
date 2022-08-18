package com.mr3y.poodle.domain

import androidx.annotation.VisibleForTesting
import com.mr3y.poodle.di.ImmediateDispatcher
import com.mr3y.poodle.network.models.Result
import com.mr3y.poodle.network.models.data
import com.mr3y.poodle.network.models.exception
import com.mr3y.poodle.repository.Artifact
import com.mr3y.poodle.repository.SearchForArtifactsRepository
import com.mr3y.poodle.repository.SearchQuery
import com.mr3y.poodle.repository.SearchResult
import com.mr3y.poodle.repository.Source
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class SearchForArtifactsUseCase @Inject constructor(
    private val searchForArtifactsRepository: SearchForArtifactsRepository,
    @ImmediateDispatcher private val coroutineContext: CoroutineContext
) {
    @VisibleForTesting
    internal var cachedSearchQuery: SearchQuery? = null

    @VisibleForTesting
    internal var jitPackCachedSearchResult: SearchResult? = null

    @VisibleForTesting
    internal var mavenCentralCachedSearchResult: SearchResult? = null

    suspend operator fun invoke(
        searchQuery: SearchQuery,
        isSearchOnMavenCentralEnabled: Boolean,
        isSearchOnJitPackEnabled: Boolean
    ): Flow<SearchResult?> {
        if (searchQuery == SearchQuery.EMPTY || searchQuery.text.length < 2 || (!isSearchOnMavenCentralEnabled && !isSearchOnJitPackEnabled)) {
            cachedSearchQuery = null
            mavenCentralCachedSearchResult = null
            jitPackCachedSearchResult = null
            return flowOf(null)
        }
        if (searchQuery == cachedSearchQuery && (mavenCentralCachedSearchResult != null || jitPackCachedSearchResult != null)) {
            when {
                !isSearchOnMavenCentralEnabled -> return flowOf(jitPackCachedSearchResult)
                !isSearchOnJitPackEnabled -> return flowOf(mavenCentralCachedSearchResult)
            }
        }
        return flow {
            searchForArtifactsRepository.searchByQuery(
                searchQuery,
                isSearchOnMavenCentralEnabled,
                isSearchOnJitPackEnabled
            ).collect {
                if (it.second == Source.MavenCentral) {
                    mavenCentralCachedSearchResult = if (it.first is Result.Success) it else null
                } else {
                    jitPackCachedSearchResult = if (it.first is Result.Success) it else null
                }
                cachedSearchQuery = searchQuery
                emit(it)
            }
        }.flowOn(coroutineContext)
    }
}

data class SearchUiState(
    private val mavenCentralArtifacts: Result<List<Artifact>>? = null,
    private val jitPackArtifacts: Result<List<Artifact>>? = null,
) {
    val isLoading = mavenCentralArtifacts.isLoading() || jitPackArtifacts.isLoading()
    val artifacts = listOfNotNull(mavenCentralArtifacts?.data, jitPackArtifacts?.data).flatten()
    val exceptions = listOfNotNull(mavenCentralArtifacts?.exception, jitPackArtifacts?.exception)
    val isIdle: Boolean
        get() = !isLoading && mavenCentralArtifacts == null && jitPackArtifacts == null

    val shouldShowArtifacts: Boolean
        get() = !isLoading && (!mavenCentralArtifacts.isError() && !jitPackArtifacts.isError()) && (mavenCentralArtifacts.isSuccess() || jitPackArtifacts.isSuccess())

    val shouldShowExceptions: Boolean
        get() = !isLoading && (!mavenCentralArtifacts.isSuccess() && !jitPackArtifacts.isSuccess()) && (mavenCentralArtifacts.isError() || jitPackArtifacts.isError())

    val shouldShowArtifactsAndExceptions: Boolean
        get() = !isLoading && (mavenCentralArtifacts.isSuccess() xor jitPackArtifacts.isSuccess()) && (mavenCentralArtifacts.isError() xor jitPackArtifacts.isError())

    fun reduce(
        searchResult: SearchResult,
        isSearchingOnMavenEnabled: Boolean = true,
        isSearchingOnJitPackEnabled: Boolean = true
    ): SearchUiState {
        val (artifacts, source) = searchResult
        val mavenCentralArtifacts = when {
            isSearchingOnMavenEnabled && source == Source.MavenCentral -> artifacts
            isSearchingOnMavenEnabled -> this.mavenCentralArtifacts
            else -> null
        }
        val jitPackArtifacts = when {
            isSearchingOnJitPackEnabled && source == Source.JitPack -> artifacts
            isSearchingOnJitPackEnabled -> this.jitPackArtifacts
            else -> null
        }
        return SearchUiState(
            mavenCentralArtifacts = mavenCentralArtifacts,
            jitPackArtifacts = jitPackArtifacts
        )
    }

    private fun <T> Result<T>?.isLoading() = this is Result.Loading

    private fun <T> Result<T>?.isSuccess() = this is Result.Success

    private fun <T> Result<T>?.isError() = this is Result.Error

    companion object {
        val Initial = SearchUiState()
    }
}

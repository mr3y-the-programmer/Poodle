package com.mr3y.poodle.domain

import androidx.annotation.VisibleForTesting
import com.mr3y.poodle.di.ImmediateDispatcher
import com.mr3y.poodle.network.models.Result
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
                    mavenCentralCachedSearchResult = it
                } else {
                    jitPackCachedSearchResult = it
                }
                cachedSearchQuery = searchQuery
                emit(it)
            }
        }.flowOn(coroutineContext)
    }
}

data class SearchUiState(
    val mavenCentralArtifacts: Result<List<Artifact>>? = null,
    val jitPackArtifacts: Result<List<Artifact>>? = null,
) {
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

    companion object {
        val Initial = SearchUiState()
    }
}

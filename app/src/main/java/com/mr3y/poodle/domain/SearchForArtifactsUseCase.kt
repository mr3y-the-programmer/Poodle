package com.mr3y.poodle.domain

import com.mr3y.poodle.network.models.Result
import com.mr3y.poodle.repository.Artifact
import com.mr3y.poodle.repository.SearchForArtifactsRepository
import com.mr3y.poodle.repository.SearchQuery
import com.mr3y.poodle.repository.SearchResult
import com.mr3y.poodle.repository.Source
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchForArtifactsUseCase @Inject constructor(
    private val searchForArtifactsRepository: SearchForArtifactsRepository
) {
    operator fun invoke(
        searchQuery: SearchQuery,
        isSearchOnMavenCentralEnabled: Boolean,
        isSearchOnJitPackEnabled: Boolean
    ): Flow<SearchResult?> {
        return searchForArtifactsRepository.searchByQuery(
            searchQuery,
            isSearchOnMavenCentralEnabled,
            isSearchOnJitPackEnabled
        )
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

package com.mr3y.poodle.domain

import com.mr3y.poodle.network.models.Result
import com.mr3y.poodle.repository.Artifact
import com.mr3y.poodle.repository.SearchForArtifactsRepository
import com.mr3y.poodle.repository.SearchQuery
import com.mr3y.poodle.repository.SearchResult
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
    companion object {
        val Initial = SearchUiState()
    }
}

package com.mr3y.poodle.repository

import com.mr3y.poodle.network.models.Result
import kotlinx.coroutines.flow.Flow

typealias SearchResult = Pair<Result<List<Artifact>>, Source>

interface SearchForArtifactsRepository {
    fun searchByQuery(
        searchQuery: SearchQuery,
        searchOnMaven: Boolean = true,
        searchOnJitPack: Boolean = true,
    ): Flow<SearchResult>
}

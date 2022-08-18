package com.mr3y.poodle.repository

import kotlinx.coroutines.flow.Flow

class FakeSearchForArtifactsRepository(private val seed: Flow<SearchResult>) : SearchForArtifactsRepository {

    // TODO:

    override fun searchByQuery(
        searchQuery: SearchQuery,
        searchOnMaven: Boolean,
        searchOnJitPack: Boolean
    ): Flow<SearchResult> {
        return seed
    }
}

package com.mr3y.poodle.repository

import kotlinx.coroutines.flow.Flow

class FakeSearchForArtifactsRepository : SearchForArtifactsRepository {

    // TODO:

    override fun searchByQuery(
        searchQuery: SearchQuery,
        searchOnMaven: Boolean,
        searchOnJitPack: Boolean
    ): Flow<SearchResult> {
        TODO("Not yet implemented")
    }
}

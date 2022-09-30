package com.mr3y.poodle.repository.fixtures

import com.mr3y.poodle.repository.Metadata
import com.mr3y.poodle.repository.SearchForArtifactsRepository
import com.mr3y.poodle.repository.SearchQuery
import com.mr3y.poodle.repository.SearchResult
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

    override fun getSearchResultMetadata(): Metadata {
        TODO("Not yet implemented")
    }
}

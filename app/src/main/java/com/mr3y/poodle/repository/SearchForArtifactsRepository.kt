package com.mr3y.poodle.repository

import com.mr3y.poodle.network.models.Result
import kotlinx.coroutines.flow.Flow

interface SearchForArtifactsRepository {
    fun searchByQuery(
        searchQuery: SearchQuery,
        searchOnMaven: Boolean = true,
        searchOnJitPack: Boolean = true,
    ): Flow<Result<List<Artifact>>>
}

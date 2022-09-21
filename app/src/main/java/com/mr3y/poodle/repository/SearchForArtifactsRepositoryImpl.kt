package com.mr3y.poodle.repository

import androidx.annotation.VisibleForTesting
import com.mr3y.poodle.network.datasources.JitPack
import com.mr3y.poodle.network.datasources.MavenCentral
import com.mr3y.poodle.network.models.JitPackResponse
import com.mr3y.poodle.network.models.MavenCentralResponse
import com.mr3y.poodle.network.models.Result
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import javax.inject.Inject

class SearchForArtifactsRepositoryImpl @Inject constructor(
    private val mavenCentralDataSource: MavenCentral,
    private val jitPackDataSource: JitPack
) : SearchForArtifactsRepository {

    @VisibleForTesting
    internal var cachedSearchQuery: SearchQuery? = null

    @VisibleForTesting
    internal var jitPackCachedSearchResult: SearchResult? = null

    @VisibleForTesting
    internal var mavenCentralCachedSearchResult: SearchResult? = null

    override fun searchByQuery(
        searchQuery: SearchQuery,
        searchOnMaven: Boolean,
        searchOnJitPack: Boolean,
    ): Flow<SearchResult?> {
        if (searchQuery == SearchQuery.EMPTY || searchQuery.text.length < 2 || (!searchOnMaven && !searchOnJitPack)) {
            cachedSearchQuery = null
            mavenCentralCachedSearchResult = null
            jitPackCachedSearchResult = null
            return flowOf(null)
        }

        if (searchQuery == cachedSearchQuery && (mavenCentralCachedSearchResult != null || jitPackCachedSearchResult != null)) {
            Napier.i("Retrieving Cached result, since nothing has changed in the search query")
            when {
                !searchOnMaven && jitPackCachedSearchResult != null -> return flowOf(jitPackCachedSearchResult)
                !searchOnJitPack && mavenCentralCachedSearchResult != null -> return flowOf(mavenCentralCachedSearchResult)
            }
        }

        val mavenCentralArtifacts by lazy {
            mavenCentralDataSource.getArtifacts {
                text = searchQuery.text
                groupId = searchQuery.groupId
                limit = searchQuery.limit ?: limit
                packaging = searchQuery.packaging
                tags = searchQuery.tags
                containsClassSimpleName = searchQuery.containsClassSimpleName
                containsClassFullyQualifiedName = searchQuery.containsClassFullyQualifiedName
            }.map {
                val data = when (it) {
                    is Result.Success -> { Result.Success(it.data.toArtifacts()) }
                    is Result.Error -> { it }
                    is Result.Loading -> { it }
                }
                SearchResult(data, Source.MavenCentral).also { searchResult -> mavenCentralCachedSearchResult = searchResult }
            }
        }
        val jitPackArtifacts by lazy {
            jitPackDataSource.getArtifacts {
                text = searchQuery.text
                groupId = searchQuery.groupId
                limit = searchQuery.limit ?: limit
            }.map {
                val data = when (it) {
                    is Result.Success -> { Result.Success(it.data.toArtifacts()) }
                    is Result.Error -> { it }
                    is Result.Loading -> { it }
                }
                SearchResult(data, Source.JitPack).also { searchResult -> jitPackCachedSearchResult = searchResult }
            }
        }

        cachedSearchQuery = searchQuery
        Napier.i("$searchQuery has been cached!")
        return when {
            searchOnMaven && searchOnJitPack -> merge(mavenCentralArtifacts, jitPackArtifacts)
            searchOnMaven && !searchOnJitPack -> mavenCentralArtifacts
            else -> jitPackArtifacts
        }
    }

    private fun MavenCentralResponse.toArtifacts(): List<Artifact.MavenCentralArtifact> {
        return response.docs.map {
            Artifact.MavenCentralArtifact(
                it.id,
                it.latestVersion,
                it.packaging,
                ZonedDateTime.ofInstant(
                    Instant.ofEpochMilli(it.timestamp),
                    ZoneId.systemDefault()
                )
            )
        }
    }

    private fun JitPackResponse.toArtifacts(): List<Artifact.JitPackArtifact> {
        return artifacts.map {
            Artifact.JitPackArtifact(
                it.groupAndArtifactIdCoordinates,
                it.version,
                if (it.timestamp == null) null else ZonedDateTime.ofInstant(Instant.ofEpochMilli(it.timestamp), ZoneId.systemDefault())
            )
        }
    }
}

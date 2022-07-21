package com.mr3y.poodle.repository

import com.mr3y.poodle.network.datasources.JitPack
import com.mr3y.poodle.network.datasources.MavenCentral
import com.mr3y.poodle.network.models.JitPackResponse
import com.mr3y.poodle.network.models.MavenCentralResponse
import com.mr3y.poodle.network.models.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
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

    override fun searchByQuery(
        searchQuery: SearchQuery,
        searchOnMaven: Boolean,
        searchOnJitPack: Boolean,
    ): Flow<Result<List<Artifact>>> {
        val mavenCentralArtifacts by lazy {
            mavenCentralDataSource.getArtifacts {
                text = searchQuery.text
                groupId = searchQuery.groupId
                limit = searchQuery.limit
                packaging = searchQuery.packaging
                tags = searchQuery.tags
                containsClassSimpleName = searchQuery.containsClassSimpleName
                containsClassFullyQualifiedName = searchQuery.containsClassFullyQualifiedName
            }.map {
                when (it) {
                    is Result.Success -> { Result.Success(it.data.toArtifacts()) }
                    is Result.Error -> { it }
                    is Result.Loading -> { it }
                }
            }
        }
        val jitPackArtifacts by lazy {
            jitPackDataSource.getArtifacts {
                text = searchQuery.text
                groupId = searchQuery.groupId
                limit = searchQuery.limit
            }.map {
                when (it) {
                    is Result.Success -> { Result.Success(it.data.toArtifacts()) }
                    is Result.Error -> { it }
                    is Result.Loading -> { it }
                }
            }
        }

        return when {
            searchOnMaven && searchOnJitPack -> merge(mavenCentralArtifacts, jitPackArtifacts)
            searchOnMaven && !searchOnJitPack -> mavenCentralArtifacts
            !searchOnMaven && searchOnJitPack -> jitPackArtifacts
            else -> emptyFlow()
        }
    }

    private fun MavenCentralResponse.toArtifacts(): List<Artifact.MavenCentralArtifact> {
        return artifacts.map {
            Artifact.MavenCentralArtifact(
                it.id,
                it.latestVersion,
                it.packaging,
                ZonedDateTime.ofInstant(Instant.ofEpochMilli(it.timestamp), ZoneId.systemDefault())
            )
        }
    }

    private fun JitPackResponse.toArtifacts(): List<Artifact.JitPackArtifact> {
        return artifacts.map { Artifact.JitPackArtifact(it.id) }
    }
}

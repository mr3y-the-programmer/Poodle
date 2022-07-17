package com.mr3y.poodle.network.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MavenCentralResponse(
    val numFound: Int,
    val start: Int,
    @SerialName("docs")
    val artifacts: List<MavenCentralArtifact>
)

@Serializable
data class MavenCentralArtifact(
    val id: String,
    @SerialName("g")
    val groupId: String,
    @SerialName("a")
    val artifactName: String,
    val latestVersion: String,
    @SerialName("p")
    val packaging: String,
    val timestamp: Long,
)

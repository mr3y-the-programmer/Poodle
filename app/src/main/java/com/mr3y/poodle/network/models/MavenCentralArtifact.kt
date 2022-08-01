package com.mr3y.poodle.network.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MavenCentralResponse(
    val responseHeader: MavenCentralResponseHeader,
    val response: ResponseBody,
)

@Serializable
data class ResponseBody(
    val numFound: Int,
    val start: Int,
    val docs: List<MavenCentralArtifact>
)

// This is not the structure of the header returned by the endpoint. it is a Dummy object. it exists because
// the Json Parser has to match its brackets as part of the decoding process, otherwise we would get a JsonDecoding exception
@Serializable
data class MavenCentralResponseHeader(
    val id: String? = null
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

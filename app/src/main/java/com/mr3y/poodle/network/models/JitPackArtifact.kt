package com.mr3y.poodle.network.models

import kotlinx.serialization.Serializable

@Serializable
internal data class PartialJitPackResponse(
    val coordinates: List<JitPackArtifactCoordinates>
)

@Serializable
internal data class JitPackArtifactCoordinates(
    val fullIdCoordinate: String
)

@Serializable
data class JitPackResponse(
    val artifacts: List<JitPackArtifact>
)

@Serializable
data class JitPackArtifact(
    val groupAndArtifactIdCoordinates: String,
    val version: String?,
    val timestamp: Long?,
)

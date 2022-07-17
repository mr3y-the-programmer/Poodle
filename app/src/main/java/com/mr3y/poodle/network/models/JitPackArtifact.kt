package com.mr3y.poodle.network.models

import kotlinx.serialization.Serializable

@Serializable
data class JitPackResponse(
    val artifacts: List<JitPackArtifact>
)

@Serializable
data class JitPackArtifact(
    val id: String,
)

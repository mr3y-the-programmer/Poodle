package com.mr3y.poodle.repository

import java.time.ZonedDateTime

sealed class Artifact(open val fullId: String) {
    data class MavenCentralArtifact(
        override val fullId: String,
        val latestVersion: String,
        val packaging: String,
        val lastUpdated: ZonedDateTime
    ) : Artifact(fullId)

    data class JitPackArtifact(
        override val fullId: String,
    ) : Artifact(fullId)
}

enum class Source {
    MavenCentral,

    JitPack
}

package com.mr3y.poodle.repository

import java.time.ZonedDateTime

sealed class Artifact(open val fullId: String, val source: Source) {
    data class MavenCentralArtifact(
        override val fullId: String,
        val latestVersion: String,
        val packaging: String,
        val lastUpdated: ZonedDateTime
    ) : Artifact(fullId, Source.MavenCentral)

    data class JitPackArtifact(
        override val fullId: String,
    ) : Artifact(fullId, Source.JitPack)
}

enum class Source {
    MavenCentral,

    JitPack
}

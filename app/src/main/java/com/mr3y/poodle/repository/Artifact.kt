package com.mr3y.poodle.repository

import java.time.ZonedDateTime

sealed class Artifact(open val fullId: String, open val latestVersion: String?, open val lastUpdated: ZonedDateTime?) {
    data class MavenCentralArtifact(
        override val fullId: String,
        override val latestVersion: String,
        val packaging: String,
        override val lastUpdated: ZonedDateTime
    ) : Artifact(fullId, latestVersion, lastUpdated)

    data class JitPackArtifact(
        override val fullId: String,
        override val latestVersion: String?,
        override val lastUpdated: ZonedDateTime?
    ) : Artifact(fullId, latestVersion, lastUpdated)
}

enum class Source {
    MavenCentral,

    JitPack
}

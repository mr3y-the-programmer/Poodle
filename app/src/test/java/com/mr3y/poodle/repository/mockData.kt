package com.mr3y.poodle.repository

import com.mr3y.poodle.network.fakeMavenCentralDeserializedResponse
import com.mr3y.poodle.network.filteredFakeJitPackDeSerializedResponse
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

internal val fakeSearchQuery = SearchQuery("zhuinden", "", 100, "", emptySet(), "", "")

internal val fakeArtifacts = listOf(
    Artifact.MavenCentralArtifact(
        "io.github.a914-gowtham:compose-ratingbar",
        "1.2.3",
        "aar",
        ZonedDateTime.ofInstant(Instant.ofEpochMilli(fakeMavenCentralDeserializedResponse.response.docs[0].timestamp), ZoneId.systemDefault())
    ),
    Artifact.MavenCentralArtifact(
        "de.charlex.compose:speeddial",
        "1.0.0-beta03",
        "aar",
        ZonedDateTime.ofInstant(Instant.ofEpochMilli(fakeMavenCentralDeserializedResponse.response.docs[1].timestamp), ZoneId.systemDefault())
    ),
    Artifact.JitPackArtifact("com.github.zhuinden:simple-stack-extensions", "1.0.1", ZonedDateTime.ofInstant(Instant.ofEpochMilli(filteredFakeJitPackDeSerializedResponse.artifacts[0].timestamp!!), ZoneId.systemDefault())),
    Artifact.JitPackArtifact("com.github.zhuinden:simple-stack", "1.0.1", ZonedDateTime.ofInstant(Instant.ofEpochMilli(filteredFakeJitPackDeSerializedResponse.artifacts[1].timestamp!!), ZoneId.systemDefault())),
    Artifact.JitPackArtifact("com.github.zhuinden:state-bundle", "1.0.1", ZonedDateTime.ofInstant(Instant.ofEpochMilli(filteredFakeJitPackDeSerializedResponse.artifacts[2].timestamp!!), ZoneId.systemDefault())),
    Artifact.JitPackArtifact("com.github.zhuinden:realm-monarchy", "1.0.1", ZonedDateTime.ofInstant(Instant.ofEpochMilli(filteredFakeJitPackDeSerializedResponse.artifacts[3].timestamp!!), ZoneId.systemDefault())),
    Artifact.JitPackArtifact("com.github.zhuinden:fragmentviewbindingdelegate-kt", "1.0.1", ZonedDateTime.ofInstant(Instant.ofEpochMilli(filteredFakeJitPackDeSerializedResponse.artifacts[4].timestamp!!), ZoneId.systemDefault())),
)

internal val fakeArtifactsPart1 = fakeArtifacts.take(2)

internal val fakeArtifactsPart2 = fakeArtifacts.drop(2)

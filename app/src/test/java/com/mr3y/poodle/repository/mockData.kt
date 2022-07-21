package com.mr3y.poodle.repository

import java.time.ZonedDateTime

internal val fakeSearchQuery = SearchQuery("zhuinden", "", 100, "", emptySet(), "", "")

internal val fakeArtifacts = listOf(
    Artifact.MavenCentralArtifact(
        "io.github.a914-gowtham:compose-ratingbar",
        "1.2.3",
        "aar",
        ZonedDateTime.parse("2022-02-04T20:47:08+02:00[Africa/Cairo]")
    ),
    Artifact.MavenCentralArtifact(
        "de.charlex.compose:speeddial",
        "1.0.0-beta03",
        "aar",
        ZonedDateTime.parse("2021-06-16T20:34:06+02:00[Africa/Cairo]")
    ),
    Artifact.JitPackArtifact("com.github.zhuinden:simple-stack-extensions"),
    Artifact.JitPackArtifact("com.github.zhuinden:simple-stack"),
    Artifact.JitPackArtifact("com.github.zhuinden:state-bundle"),
    Artifact.JitPackArtifact("com.github.zhuinden:realm-monarchy"),
    Artifact.JitPackArtifact("com.github.zhuinden:fragmentviewbindingdelegate-kt"),
)

internal val fakeArtifactsPart1 = fakeArtifacts.take(2)

internal val fakeArtifactsPart2 = fakeArtifacts.drop(2)

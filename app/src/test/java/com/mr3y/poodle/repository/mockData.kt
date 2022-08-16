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
    Artifact.JitPackArtifact("com.github.zhuinden:simple-stack-extensions", "1.0.1", ZonedDateTime.parse("2020-12-14T18:23:59.390+02:00[Africa/Cairo]")),
    Artifact.JitPackArtifact("com.github.zhuinden:simple-stack", "1.0.1", ZonedDateTime.parse("2020-12-14T18:23:59.390+02:00[Africa/Cairo]")),
    Artifact.JitPackArtifact("com.github.zhuinden:state-bundle", "1.0.1", ZonedDateTime.parse("2020-12-14T18:23:59.390+02:00[Africa/Cairo]")),
    Artifact.JitPackArtifact("com.github.zhuinden:realm-monarchy", "1.0.1", ZonedDateTime.parse("2020-12-14T18:23:59.390+02:00[Africa/Cairo]")),
    Artifact.JitPackArtifact("com.github.zhuinden:fragmentviewbindingdelegate-kt", "1.0.1", ZonedDateTime.parse("2020-12-14T18:23:59.390+02:00[Africa/Cairo]")),
)

internal val fakeArtifactsPart1 = fakeArtifacts.take(2)

internal val fakeArtifactsPart2 = fakeArtifacts.drop(2)

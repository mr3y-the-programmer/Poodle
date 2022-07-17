package com.mr3y.poodle.network

import com.mr3y.poodle.network.models.MavenCentralArtifact
import com.mr3y.poodle.network.models.MavenCentralResponse

internal val fakeMavenCentralSerializedResponse = """
    {
      "numFound": 2,
      "start": 0,
      "docs": [
        {
          "id": "io.github.a914-gowtham:compose-ratingbar",
          "g": "io.github.a914-gowtham",
          "a": "compose-ratingbar",
          "latestVersion": "1.2.3",
          "repositoryId": "central",
          "p": "aar",
          "timestamp": 1644000428000,
          "versionCount": 13,
          "text": [
            "io.github.a914-gowtham",
            "compose-ratingbar",
            "-javadoc.jar",
            "-sources.jar",
            ".aar",
            ".module",
            ".pom"
          ],
          "ec": [
            "-javadoc.jar",
            "-sources.jar",
            ".aar",
            ".module",
            ".pom"
          ]
        },
        {
          "id": "de.charlex.compose:speeddial",
          "g": "de.charlex.compose",
          "a": "speeddial",
          "latestVersion": "1.0.0-beta03",
          "repositoryId": "central",
          "p": "aar",
          "timestamp": 1623868446000,
          "versionCount": 7,
          "text": [
            "de.charlex.compose",
            "speeddial",
            ".pom.asc.sha256",
            "-sources.jar",
            ".aar",
            ".aar.asc.sha512",
            "-sources.jar.sha512",
            ".aar.sha512",
            ".pom.asc.sha512",
            ".pom.sha256",
            "-sources.jar.sha256",
            ".aar.asc.sha256",
            "-sources.jar.asc.sha512",
            ".aar.sha256",
            ".pom.sha512",
            ".pom",
            "-sources.jar.asc.sha256"
          ],
          "ec": [
            ".pom.asc.sha256",
            "-sources.jar",
            ".aar",
            ".aar.asc.sha512",
            "-sources.jar.sha512",
            ".aar.sha512",
            ".pom.asc.sha512",
            ".pom.sha256",
            "-sources.jar.sha256",
            ".aar.asc.sha256",
            "-sources.jar.asc.sha512",
            ".aar.sha256",
            ".pom.sha512",
            ".pom",
            "-sources.jar.asc.sha256"
          ]
        }
      ]
    }
""".trimIndent()

internal val fakeMavenCentralDeserializedResponse = MavenCentralResponse(
    numFound = 2,
    start = 0,
    listOf(
        MavenCentralArtifact(
            "io.github.a914-gowtham:compose-ratingbar",
            "io.github.a914-gowtham",
            "compose-ratingbar",
            "1.2.3",
            "aar",
            1644000428000
        ),
        MavenCentralArtifact(
            "de.charlex.compose:speeddial",
            "de.charlex.compose",
            "speeddial",
            "1.0.0-beta03",
            "aar",
            1623868446000
        )
    )
)

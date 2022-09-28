package com.mr3y.poodle.network.fixtures

import com.mr3y.poodle.network.fixtures.internal.mockEngine
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.ANDROID
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

const val mavenCentralEndpointUrl = "https://search.maven.org/solrsearch/select"

const val jitpackEndpointUrl = "https://jitpack.io/api"

val fakeClient: HttpClient = HttpClient(mockEngine) {
    install(Logging) {
        logger = Logger.ANDROID
        level = LogLevel.HEADERS
    }
    install(ContentNegotiation) {
        json(
            Json {
                ignoreUnknownKeys = true
                encodeDefaults = true
                isLenient = true
                allowSpecialFloatingPointValues = true
                allowStructuredMapKeys = true
                prettyPrint = false
                useArrayPolymorphism = false
            }
        )
    }
}

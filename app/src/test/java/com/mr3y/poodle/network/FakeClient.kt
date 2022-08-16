package com.mr3y.poodle.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.ANDROID
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.HttpHeaders
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import io.ktor.utils.io.ByteReadChannel
import kotlinx.serialization.json.Json
import java.util.Queue

internal val mavenCentralTestUrl = "https://search.maven.org/solrsearch/select"

internal val jitpackTestUrl = "https://jitpack.io/api"

private val mockEngine: (responses: Queue<String>) -> MockEngine = {
    MockEngine { request ->
        respond(
            content = ByteReadChannel(it.poll()!!),
            headers = headersOf(HttpHeaders.ContentType, "application/json")
        )
    }
}

internal val fakeClient: (responses: Queue<String>) -> HttpClient = {
    HttpClient(mockEngine(it)) {
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
}

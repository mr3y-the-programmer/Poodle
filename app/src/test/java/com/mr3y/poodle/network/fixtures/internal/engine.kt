package com.mr3y.poodle.network.fixtures.internal

import com.mr3y.poodle.network.fixtures.MockResponseResult
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.respondError
import io.ktor.http.HttpHeaders
import io.ktor.http.headersOf

internal val handler = hashMapOf<String, MockResponseResult>()

internal val mockEngine: MockEngine = MockEngine { request ->
    when (val response = handler[request.url.toString()]) {
        is MockResponseResult.ResponseOk -> {
            respond(
                content = response.content,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }
        is MockResponseResult.ResponseError -> {
            val statusCode = response.statusCode
            respondError(status = statusCode, headers = headersOf(HttpHeaders.ContentType, "application/json"))
        }
        null -> {
            throw IllegalArgumentException(
                "Your request isn't mocked, make sure to mock it first before trying to hit the client, because the client cannot " +
                    "make a real network request in a test environment"
            )
        }
    }
}

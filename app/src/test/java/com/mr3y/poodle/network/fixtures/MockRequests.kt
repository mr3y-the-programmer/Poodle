package com.mr3y.poodle.network.fixtures

import com.mr3y.poodle.network.fixtures.internal.handler
import io.ktor.http.HttpStatusCode

fun onRequest(
    url: String,
    response: () -> MockResponseResult
) { handler[url] = response() }

fun respondOkWithContent(content: String) = MockResponseResult.ResponseOk(content)

fun respondError(statusCode: HttpStatusCode = HttpStatusCode.BadRequest) = MockResponseResult.ResponseError(statusCode)

sealed interface MockResponseResult {
    class ResponseOk(val content: String) : MockResponseResult
    class ResponseError(val statusCode: HttpStatusCode) : MockResponseResult
}

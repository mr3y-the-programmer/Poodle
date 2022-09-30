package com.mr3y.poodle.network.datasources

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.mr3y.poodle.network.MavenCentralQueryParameters
import com.mr3y.poodle.network.fakeMavenCentralDeserializedResponse
import com.mr3y.poodle.network.fakeMavenCentralSerializedResponse
import com.mr3y.poodle.network.fixtures.fakeClient
import com.mr3y.poodle.network.fixtures.mavenCentralEndpointUrl
import com.mr3y.poodle.network.fixtures.onRequest
import com.mr3y.poodle.network.fixtures.respondError
import com.mr3y.poodle.network.fixtures.respondOkWithContent
import com.mr3y.poodle.network.invalidMavenCentralSerializedResponse
import com.mr3y.poodle.network.models.Result
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.BeforeClass
import org.junit.Test
import org.junit.experimental.runners.Enclosed
import org.junit.runner.RunWith

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(Enclosed::class)
class MavenCentralImplTest {

    class MavenCentralQueryParametersFormattingTest {
        @Test
        fun `given all query parameters in DSL, then verifies they are normalized correctly`() {
            val actualQuery = MavenCentralQueryParameters.apply {
                text = "guice"
                groupId = "com.google.inject"
                packaging = "jar"
                containsClassFullyQualifiedName = "com.google.inject.matcher.Matchers"
                tags = setOf("dependency", "guice", "injection", "java")
            }.getNormalizedStringQueryParameter()
            val expectedQuery = "guice AND g:com.google.inject AND p:jar AND fc:com.google.inject.matcher.Matchers AND tags:dependency AND tags:guice AND tags:injection AND tags:java"
            assertThat(actualQuery).isEqualTo(expectedQuery)
        }

        @Test
        fun `given some query parameters in DSL, then verifies they are normalized correctly`() {
            val actualQuery = MavenCentralQueryParameters.apply {
                groupId = "com.google.inject"
                packaging = "jar"
                tags = setOf("dependency", "guice", "injection", "java")
            }.getNormalizedStringQueryParameter()
            val expectedQuery = "g:com.google.inject AND p:jar AND tags:dependency AND tags:guice AND tags:injection AND tags:java"
            assertThat(actualQuery).isEqualTo(expectedQuery)
        }

        @Test
        fun `given unsupported filtering packaging in DSL, then verifies the query is normalized & the packaging is omitted`() {
            val actualQuery = MavenCentralQueryParameters.apply {
                groupId = "com.google.inject"
                packaging = "klib"
                tags = setOf("dependency", "guice", "injection", "java")
            }.getNormalizedStringQueryParameter()
            val expectedQuery = "g:com.google.inject AND tags:dependency AND tags:guice AND tags:injection AND tags:java"
            assertThat(actualQuery).isEqualTo(expectedQuery)
        }

        @Test
        fun `given no query parameters, then verifies that it returns null`() {
            val actualQuery = MavenCentralQueryParameters.getNormalizedStringQueryParameter()
            assertThat(actualQuery).isEqualTo(null)
        }

        @After
        fun clearQueryParametersState() {
            MavenCentralQueryParameters.clearQueryParameters()
        }
    }

    class MavenCentralRequestDataTest {

        @Test
        fun `given a normal serialized response, then verifies it deserializes the response correctly`() = runTest {
            onRequest("$mavenCentralEndpointUrl?rows=200&start=0&wt=json") {
                respondOkWithContent(fakeMavenCentralSerializedResponse)
            }
            sut.getArtifacts {}.test {
                assertThat(awaitItem()).isEqualTo(Result.Loading)
                assertThat(awaitItem()).isEqualTo(Result.Success(fakeMavenCentralDeserializedResponse))
                awaitComplete()
            }
        }

        @Test
        fun `given a malformed serialized response, then verifies it catches & emits the error downstream`() = runTest {
            val (text, groupId, limit) = arrayOf("compose", "com.google.*", "20")
            onRequest("$mavenCentralEndpointUrl?q=$text&q:$groupId&rows=$limit&start=0&wt=json") {
                respondOkWithContent(invalidMavenCentralSerializedResponse)
            }
            sut.getArtifacts {
                this.text = text
                this.groupId = groupId
                this.limit = limit.toInt()
            }.test {
                assertThat(awaitItem()).isEqualTo(Result.Loading)
                val nextItem = awaitItem()
                assertThat(nextItem).isInstanceOf(Result.Error::class.java)
                awaitComplete()
            }
        }

        @Test
        fun `given an error response from server, then verifies it catches & emits the error downstream`() = runTest {
            onRequest("$mavenCentralEndpointUrl?rows=200&start=0&wt=json") {
                respondError(HttpStatusCode.NotFound)
            }
            sut.getArtifacts {}.test {
                assertThat(awaitItem()).isEqualTo(Result.Loading)
                val nextItem = awaitItem()
                assertThat(nextItem).isInstanceOf(Result.Error::class.java)
                awaitComplete()
            }
        }

        companion object {

            private lateinit var sut: MavenCentralImpl

            @BeforeClass
            @JvmStatic
            fun setUp() {
                sut = MavenCentralImpl(fakeClient, mavenCentralEndpointUrl)
            }
        }
    }
}

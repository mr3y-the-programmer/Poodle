package com.mr3y.poodle.network.datasources

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.mr3y.poodle.network.JitPackQueryParameters
import com.mr3y.poodle.network.fakeJitPackDeserializedResponse
import com.mr3y.poodle.network.fakeJitPackResponseMetadata
import com.mr3y.poodle.network.fakeJitPackSerializedResponse
import com.mr3y.poodle.network.filteredFakeJitPackDeSerializedResponse
import com.mr3y.poodle.network.filteredFakeJitPackResponseMetadata
import com.mr3y.poodle.network.filteredFakeJitPackSerializedResponse
import com.mr3y.poodle.network.fixtures.fakeClient
import com.mr3y.poodle.network.fixtures.jitpackEndpointUrl
import com.mr3y.poodle.network.fixtures.onRequest
import com.mr3y.poodle.network.fixtures.respondError
import com.mr3y.poodle.network.fixtures.respondOkWithContent
import com.mr3y.poodle.network.invalidJitPackSerializedResponse
import com.mr3y.poodle.network.models.Result
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.BeforeClass
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class JitPackImplTest {

    @Test
    fun `given a normal serialized response & no query parameters, then verifies it deserializes the response correctly`() = runTest {
        onRequest("$jitpackEndpointUrl/search?limit=50") {
            respondOkWithContent(fakeJitPackSerializedResponse)
        }
        fakeJitPackSerializedResponse.forEachArtifactIndexed { index, groupId, artifactName ->
            onRequest("$jitpackEndpointUrl/builds/$groupId/$artifactName/latestOk") {
                respondOkWithContent(fakeJitPackResponseMetadata[index])
            }
        }

        sut.getArtifacts { }.test {
            assertThat(awaitItem()).isEqualTo(Result.Loading)
            assertThat(awaitItem()).isEqualTo(Result.Success(fakeJitPackDeserializedResponse))
            awaitComplete()
        }
    }

    @Test
    fun `given a serialized response with all query parameters, then verifies it deserializes & filters the response correctly`() = runTest {
        val groupId = "com.github.zhuinden"
        val limit = 3
        onRequest("$jitpackEndpointUrl/search?q=$groupId%3A&limit=$limit") {
            respondOkWithContent(filteredFakeJitPackSerializedResponse)
        }
        filteredFakeJitPackSerializedResponse.forEachArtifactIndexed { index, _, artifactName ->
            onRequest("$jitpackEndpointUrl/builds/$groupId/$artifactName/latestOk") {
                respondOkWithContent(filteredFakeJitPackResponseMetadata[index])
            }
        }
        sut.getArtifacts {
            this.groupId = groupId
            text = "Simple-stack"
            this.limit = limit
        }.test {
            assertThat(awaitItem()).isEqualTo(Result.Loading)
            val expected = filteredFakeJitPackDeSerializedResponse.copy(artifacts = filteredFakeJitPackDeSerializedResponse.artifacts.slice(0..1))
            assertThat(awaitItem()).isEqualTo(Result.Success(expected))
            awaitComplete()
        }
    }

    @Test
    fun `given a serialized response with some query parameters, then verifies it deserializes & filters the response correctly`() = runTest {
        val query = "zhuinden"
        onRequest("$jitpackEndpointUrl/search?q=$query&limit=50") {
            respondOkWithContent(filteredFakeJitPackSerializedResponse)
        }
        filteredFakeJitPackSerializedResponse.forEachArtifactIndexed { index, groupId, artifactName ->
            onRequest("$jitpackEndpointUrl/builds/$groupId/$artifactName/latestOk") {
                respondOkWithContent(filteredFakeJitPackResponseMetadata[index])
            }
        }
        sut.getArtifacts {
            text = query
        }.test {
            assertThat(awaitItem()).isEqualTo(Result.Loading)
            assertThat(awaitItem()).isEqualTo(Result.Success(filteredFakeJitPackDeSerializedResponse))
            awaitComplete()
        }
    }

    @Test
    fun `given an invalid serialized response, then verifies it catches & emits the error downstream`() = runTest {
        val groupId = "com.github.zhuinden"
        onRequest("$jitpackEndpointUrl/search?q=$groupId%3A&limit=50") {
            respondOkWithContent(invalidJitPackSerializedResponse)
        }
        sut.getArtifacts {
            this.groupId = groupId
            text = "Simple-stack"
        }.test {
            assertThat(awaitItem()).isEqualTo(Result.Loading)
            val nextItem = awaitItem()
            assertThat(nextItem).isInstanceOf(Result.Error::class.java)
            awaitComplete()
        }
    }

    @Test
    fun `given an error response, then verifies it catches & emits the error downstream`() = runTest {
        val queryText = "compose"
        onRequest("$jitpackEndpointUrl/search?q=$queryText&limit=50") {
            respondError(HttpStatusCode.InternalServerError)
        }
        sut.getArtifacts {
            text = queryText
        }.test {
            assertThat(awaitItem()).isEqualTo(Result.Loading)
            val nextItem = awaitItem()
            assertThat(nextItem).isInstanceOf(Result.Error::class.java)
            awaitComplete()
        }
    }

    @After
    fun clearQueryParametersState() {
        JitPackQueryParameters.clearQueryParameters()
    }

    private fun String.forEachArtifactIndexed(action: (index: Int, groupId: String, artifactName: String) -> Unit) {
        removePrefix("{")
            .removeSuffix("}")
            .split(',')
            .map {
                val groupIdPlusArtifactName = it.substringBeforeLast("\":").replace("\"", "").trim()
                Pair(
                    groupIdPlusArtifactName.substringBefore(':'),
                    groupIdPlusArtifactName.substringAfter(':')
                )
            }.forEachIndexed { index, gia ->
                val (groupId, artifactName) = gia
                action(index, groupId, artifactName)
            }
    }

    companion object {

        private lateinit var sut: JitPackImpl

        @BeforeClass
        @JvmStatic
        fun setUp() {
            sut = JitPackImpl(fakeClient, jitpackEndpointUrl)
        }
    }
}

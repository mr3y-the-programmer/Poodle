package com.mr3y.poodle.network.datasources

import app.cash.turbine.test
import com.mr3y.poodle.network.JitPackQueryParameters
import com.mr3y.poodle.network.fakeClient
import com.mr3y.poodle.network.fakeJitPackDeserializedResponse
import com.mr3y.poodle.network.fakeJitPackSerializedResponse
import com.mr3y.poodle.network.filteredFakeJitPackDeSerializedResponse
import com.mr3y.poodle.network.filteredFakeJitPackSerializedResponse
import com.mr3y.poodle.network.jitpackTestUrl
import com.mr3y.poodle.network.models.Result
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class JitPackImplTest {

    @Test
    fun `given a normal serialized response & no query parameters, then verifies it deserializes the response correctly`() = runTest {
        createJitPackImplInstance(fakeJitPackSerializedResponse).getArtifacts { }.test {
            assertEquals(Result.Loading, awaitItem())
            assertEquals(Result.Success(fakeJitPackDeserializedResponse), awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `given a serialized response with all query parameters, then verifies it deserializes & filters the response correctly`() = runTest {
        createJitPackImplInstance(filteredFakeJitPackSerializedResponse).getArtifacts {
            groupId = "com.github.zhuinden"
            text = "Simple-stack"
            limit = 3
        }.test {
            assertEquals(Result.Loading, awaitItem())
            val expected = filteredFakeJitPackDeSerializedResponse.copy(artifacts = filteredFakeJitPackDeSerializedResponse.artifacts.slice(0..1))
            assertEquals(Result.Success(expected), awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `given a serialized response with some query parameters, then verifies it deserializes & filters the response correctly`() = runTest {
        createJitPackImplInstance(filteredFakeJitPackSerializedResponse).getArtifacts {
            text = "zhuinden"
        }.test {
            assertEquals(Result.Loading, awaitItem())
            assertEquals(Result.Success(filteredFakeJitPackDeSerializedResponse), awaitItem())
            awaitComplete()
        }
    }

    @After
    fun clearQueryParametersState() {
        JitPackQueryParameters.clearQueryParameters()
    }

    private fun createJitPackImplInstance(response: String): JitPackImpl {
        return JitPackImpl(fakeClient(response), jitpackTestUrl)
    }
}

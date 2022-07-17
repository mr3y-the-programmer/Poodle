package com.mr3y.poodle.network.datasources

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
        val actualResponse = createJitPackImplInstance(fakeJitPackSerializedResponse).getArtifacts { }
        assertEquals(Result.Success(fakeJitPackDeserializedResponse), actualResponse)
    }

    @Test
    fun `given a serialized response with all query parameters, then verifies it deserializes & filters the response correctly`() = runTest {
        val actualResponse = createJitPackImplInstance(filteredFakeJitPackSerializedResponse).getArtifacts {
            groupId = "com.github.zhuinden"
            text = "Simple-stack"
            limit = 3
        }
        val expected = filteredFakeJitPackDeSerializedResponse.copy(artifacts = filteredFakeJitPackDeSerializedResponse.artifacts.slice(0..1))
        assertEquals(Result.Success(expected), actualResponse)
    }

    @Test
    fun `given a serialized response with some query parameters, then verifies it deserializes & filters the response correctly`() = runTest {
        val actualResponse = createJitPackImplInstance(filteredFakeJitPackSerializedResponse).getArtifacts {
            text = "zhuinden"
        }
        assertEquals(Result.Success(filteredFakeJitPackDeSerializedResponse), actualResponse)
    }

    @After
    fun clearQueryParametersState() {
        JitPackQueryParameters.clearQueryParameters()
    }

    private fun createJitPackImplInstance(response: String): JitPackImpl {
        return JitPackImpl(fakeClient(response), jitpackTestUrl)
    }
}

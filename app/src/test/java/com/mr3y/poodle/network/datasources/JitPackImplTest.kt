package com.mr3y.poodle.network.datasources

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.mr3y.poodle.network.JitPackQueryParameters
import com.mr3y.poodle.network.exceptions.DecodingException
import com.mr3y.poodle.network.fakeClient
import com.mr3y.poodle.network.fakeJitPackDeserializedResponse
import com.mr3y.poodle.network.fakeJitPackSerializedResponse
import com.mr3y.poodle.network.filteredFakeJitPackDeSerializedResponse
import com.mr3y.poodle.network.filteredFakeJitPackSerializedResponse
import com.mr3y.poodle.network.invalidJitPackSerializedResponse
import com.mr3y.poodle.network.jitpackTestUrl
import com.mr3y.poodle.network.models.Result
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class JitPackImplTest {

    @Test
    fun `given a normal serialized response & no query parameters, then verifies it deserializes the response correctly`() = runTest {
        createJitPackImplInstance(fakeJitPackSerializedResponse).getArtifacts { }.test {
            assertThat(awaitItem()).isEqualTo(Result.Loading)
            assertThat(awaitItem()).isEqualTo(Result.Success(fakeJitPackDeserializedResponse))
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
            assertThat(awaitItem()).isEqualTo(Result.Loading)
            val expected = filteredFakeJitPackDeSerializedResponse.copy(artifacts = filteredFakeJitPackDeSerializedResponse.artifacts.slice(0..1))
            assertThat(awaitItem()).isEqualTo(Result.Success(expected))
            awaitComplete()
        }
    }

    @Test
    fun `given a serialized response with some query parameters, then verifies it deserializes & filters the response correctly`() = runTest {
        createJitPackImplInstance(filteredFakeJitPackSerializedResponse).getArtifacts {
            text = "zhuinden"
        }.test {
            assertThat(awaitItem()).isEqualTo(Result.Loading)
            assertThat(awaitItem()).isEqualTo(Result.Success(filteredFakeJitPackDeSerializedResponse))
            awaitComplete()
        }
    }

    @Test
    fun `given an invalid serialized response, then verifies it catches & emits the error downstream`() = runTest {
        createJitPackImplInstance(invalidJitPackSerializedResponse).getArtifacts {
            groupId = "com.github.zhuinden"
            text = "Simple-stack"
        }.test {
            assertThat(awaitItem()).isEqualTo(Result.Loading)
            val nextItem = awaitItem()
            assertThat(nextItem).isInstanceOf(Result.Error::class.java)
            nextItem as Result.Error
            assertThat(nextItem.exception).isInstanceOf(DecodingException::class.java)
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

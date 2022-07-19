package com.mr3y.poodle.network.datasources

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.mr3y.poodle.network.MavenCentralQueryParameters
import com.mr3y.poodle.network.fakeClient
import com.mr3y.poodle.network.fakeMavenCentralDeserializedResponse
import com.mr3y.poodle.network.fakeMavenCentralSerializedResponse
import com.mr3y.poodle.network.invalidMavenCentralSerializedResponse
import com.mr3y.poodle.network.mavenCentralTestUrl
import com.mr3y.poodle.network.models.Result
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MavenCentralImplTest {

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
        assertEquals(expectedQuery, actualQuery)
    }

    @Test
    fun `given some query parameters in DSL, then verifies they are normalized correctly`() {
        val actualQuery = MavenCentralQueryParameters.apply {
            groupId = "com.google.inject"
            packaging = "jar"
            tags = setOf("dependency", "guice", "injection", "java")
        }.getNormalizedStringQueryParameter()
        val expectedQuery = "g:com.google.inject AND p:jar AND tags:dependency AND tags:guice AND tags:injection AND tags:java"
        assertEquals(expectedQuery, actualQuery)
    }

    @Test
    fun `given no query parameters, then verifies that it returns null`() {
        val actualQuery = MavenCentralQueryParameters.getNormalizedStringQueryParameter()
        assertEquals(null, actualQuery)
    }

    @Test
    fun `given a normal serialized response, then verifies it deserializes the response correctly`() = runTest {
        val client = fakeClient(fakeMavenCentralSerializedResponse)
        MavenCentralImpl(client, mavenCentralTestUrl).getArtifacts {}.test {
            assertEquals(Result.Loading, awaitItem())
            assertEquals(Result.Success(fakeMavenCentralDeserializedResponse), awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `given an invalid serialized response, then verifies it catches & emits the error downstream`() = runTest {
        val client = fakeClient(invalidMavenCentralSerializedResponse)
        MavenCentralImpl(client, mavenCentralTestUrl).getArtifacts {}.test {
            assertEquals(Result.Loading, awaitItem())
            assertThat(awaitItem()).isInstanceOf(Result.Error::class.java)
            awaitComplete()
        }
    }

    @After
    fun clearQueryParametersState() {
        MavenCentralQueryParameters.clearQueryParameters()
    }
}

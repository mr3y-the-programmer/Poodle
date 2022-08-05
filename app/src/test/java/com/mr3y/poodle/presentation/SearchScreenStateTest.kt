package com.mr3y.poodle.presentation

import com.google.common.truth.Truth.assertThat
import com.google.common.truth.TruthJUnit.assume
import com.google.testing.junit.testparameterinjector.TestParameter
import com.google.testing.junit.testparameterinjector.TestParameterInjector
import com.google.testing.junit.testparameterinjector.TestParameters
import com.mr3y.poodle.network.exceptions.ClientException
import com.mr3y.poodle.network.exceptions.DecodingException
import com.mr3y.poodle.network.exceptions.PoodleException
import com.mr3y.poodle.network.exceptions.UnknownException
import com.mr3y.poodle.network.models.Result
import com.mr3y.poodle.repository.Artifact
import com.mr3y.poodle.repository.SearchResult
import com.mr3y.poodle.repository.Source
import org.junit.Test
import org.junit.experimental.runners.Enclosed
import org.junit.runner.RunWith
import java.time.ZonedDateTime

@RunWith(Enclosed::class)
class SearchScreenStateTest {

    @RunWith(TestParameterInjector::class)
    class StatePropertiesTest {

        @Test
        fun `given no results from either source, then make sure it is an initial state`() {
            val state = SearchScreenState(mavenCentralArtifacts = null, jitPackArtifacts = null)

            assertThat(state.isIdle).isEqualTo(true)
            assertThat(state.isLoading).isEqualTo(false)
            assertThat(state.artifacts).isEqualTo(emptyList<Artifact>())
            assertThat(state.exceptions).isEqualTo(emptyList<PoodleException>())
            assertThat(state.shouldShowArtifacts).isEqualTo(false)
            assertThat(state.shouldShowExceptions).isEqualTo(false)
            assertThat(state.shouldShowArtifactsAndExceptions).isEqualTo(false)
        }

        @Test
        fun `given at least one Loading data source, then make sure it is a loading state`(
            @TestParameter source: Source,
            @TestParameter mavenCentralResult: DataSourceResult,
            @TestParameter jitPackResult: DataSourceResult,
        ) {
            if (source == Source.MavenCentral) {
                assume().that(mavenCentralResult).isEqualTo(DataSourceResult.Loading)
            } else {
                assume().that(jitPackResult).isEqualTo(DataSourceResult.Loading)
            }

            val state = SearchScreenState(mavenCentralResult.toRealResult(), jitPackResult.toRealResult())

            assertThat(state.isIdle).isEqualTo(false)
            assertThat(state.isLoading).isEqualTo(true)
            assertThat(state.shouldShowArtifacts).isEqualTo(false)
            assertThat(state.shouldShowExceptions).isEqualTo(false)
            assertThat(state.shouldShowArtifactsAndExceptions).isEqualTo(false)
        }

        @Test
        fun `given both data sources returned artifacts, then make sure it is success state with no exceptions`() {
            val mavenCentralArtifacts = listOf(Artifact.MavenCentralArtifact("mavenCentral", "1.8", "jar", ZonedDateTime.now()))
            val jitPackArtifacts = listOf(Artifact.JitPackArtifact("jitpack"))
            val state = SearchScreenState(Result.Success(mavenCentralArtifacts), Result.Success(jitPackArtifacts))

            assertThat(state.isIdle).isEqualTo(false)
            assertThat(state.isLoading).isEqualTo(false)
            assertThat(state.artifacts).isEqualTo(mavenCentralArtifacts + jitPackArtifacts)
            assertThat(state.exceptions).isEqualTo(emptyList<PoodleException>())
            assertThat(state.shouldShowArtifacts).isEqualTo(true)
            assertThat(state.shouldShowExceptions).isEqualTo(false)
            assertThat(state.shouldShowArtifactsAndExceptions).isEqualTo(false)
        }

        @Test
        fun `given both data sources returned exceptions, then make sure it is error state with no artifacts`() {
            val state = SearchScreenState(Result.Error(DecodingException("", null)), Result.Error(UnknownException("", null)))

            assertThat(state.isIdle).isEqualTo(false)
            assertThat(state.isLoading).isEqualTo(false)
            assertThat(state.artifacts).isEqualTo(emptyList<Artifact>())
            assertThat(state.exceptions).isEqualTo(listOf(DecodingException("", null), UnknownException("", null)))
            assertThat(state.shouldShowArtifacts).isEqualTo(false)
            assertThat(state.shouldShowExceptions).isEqualTo(true)
            assertThat(state.shouldShowArtifactsAndExceptions).isEqualTo(false)
        }

        @Test
        @TestParameters("{doesMavenCentralSucceeded: true, doesJitPackSucceeded: false}")
        @TestParameters("{doesMavenCentralSucceeded: false, doesJitPackSucceeded: true}")
        fun `given either data source returned exception and the other returned artifacts, then make sure it is mixed state`(
            doesMavenCentralSucceeded: Boolean,
            doesJitPackSucceeded: Boolean
        ) {
            val state = SearchScreenState(
                if (doesMavenCentralSucceeded) Result.Success(emptyList()) else Result.Error(),
                if (doesJitPackSucceeded) Result.Success(emptyList()) else Result.Error()
            )

            assertThat(state.isIdle).isEqualTo(false)
            assertThat(state.isLoading).isEqualTo(false)
            assertThat(state.shouldShowArtifacts).isEqualTo(false)
            assertThat(state.shouldShowExceptions).isEqualTo(false)
            assertThat(state.shouldShowArtifactsAndExceptions).isEqualTo(true)
        }

        @Test
        @TestParameters("{mavenCentralResult: Success, jitPackResult: null}")
        @TestParameters("{mavenCentralResult: null, jitPackResult: Success}")
        fun `given either data source is enabled & returned artifacts while the other is disabled, then make sure it is a success state`(
            mavenCentralResult: DataSourceResult?,
            jitPackResult: DataSourceResult?,
        ) {
            val state = SearchScreenState(mavenCentralResult?.toRealResult(), jitPackResult?.toRealResult())

            assertThat(state.isIdle).isEqualTo(false)
            assertThat(state.isLoading).isEqualTo(false)
            assertThat(state.shouldShowArtifacts).isEqualTo(true)
            assertThat(state.shouldShowExceptions).isEqualTo(false)
            assertThat(state.shouldShowArtifactsAndExceptions).isEqualTo(false)
        }

        @Test
        @TestParameters("{mavenCentralResult: Error, jitPackResult: null}")
        @TestParameters("{mavenCentralResult: null, jitPackResult: Error}")
        fun `given either data source is enabled & returned exceptions while the other is disabled, then make sure it is an error state`(
            mavenCentralResult: DataSourceResult?,
            jitPackResult: DataSourceResult?,
        ) {
            val state = SearchScreenState(mavenCentralResult?.toRealResult(), jitPackResult?.toRealResult())

            assertThat(state.isIdle).isEqualTo(false)
            assertThat(state.isLoading).isEqualTo(false)
            assertThat(state.shouldShowArtifacts).isEqualTo(false)
            assertThat(state.shouldShowExceptions).isEqualTo(true)
            assertThat(state.shouldShowArtifactsAndExceptions).isEqualTo(false)
        }

        // we have to map Result<> sealed interface to an enum, because TestParameter doesn't know how to deal with sealed interfaces
        enum class DataSourceResult {
            Loading,
            Success,
            Error,
            Null
        }

        private fun DataSourceResult.toRealResult(): Result<List<Artifact>>? {
            return when (this) {
                DataSourceResult.Loading -> Result.Loading
                DataSourceResult.Success -> Result.Success(emptyList())
                DataSourceResult.Error -> Result.Error()
                DataSourceResult.Null -> null
            }
        }
    }

    class StateModificationTest {
        @Test
        fun `given an initial state, when we try to reduce it a number of times, then make sure every reduced state is a valid state`() {
            val state0 = SearchScreenState.Initial
            state0.assertIsIdle()

            val state1 = state0.reduce(SearchResult(Result.Loading, Source.MavenCentral))
            state1.assertIsLoading()

            val state2 = state1.reduce(SearchResult(Result.Loading, Source.JitPack))
            state2.assertIsLoading()

            val artifacts = listOf(Artifact.MavenCentralArtifact("mavenCentral", "1.7", "aar", ZonedDateTime.now()))
            val state3 = state2.reduce(SearchResult(Result.Success(artifacts), Source.MavenCentral))
            state3.assertIsLoading()
            state3.assertArtifactsNotEmpty()

            val state4 = state3.reduce(SearchResult(Result.Error(ClientException("", null)), Source.JitPack))
            state4.assertArtifactsAndExceptionsNotEmpty()

            val state5 = state4.reduce(SearchResult(Result.Loading, Source.JitPack), isSearchingOnMavenEnabled = false)
            state5.assertIsLoading()
            state5.assertArtifactsAndExceptionsEmpty()

            val state6 = state4.reduce(SearchResult(Result.Loading, Source.MavenCentral), isSearchingOnJitPackEnabled = false)
            state6.assertIsLoading()
            state6.assertArtifactsAndExceptionsEmpty()
        }

        private fun SearchScreenState.assertIsIdle() {
            assertThat(isIdle).isEqualTo(true)
            assertThat(isLoading).isEqualTo(false)
        }

        private fun SearchScreenState.assertIsLoading() {
            assertThat(isIdle).isEqualTo(false)
            assertThat(isLoading).isEqualTo(true)
        }

        private fun SearchScreenState.assertArtifactsNotEmpty() {
            assertThat(shouldShowArtifacts).isEqualTo(false)
            assertThat(shouldShowExceptions).isEqualTo(false)
            assertThat(artifacts).isNotEqualTo(emptyList<Artifact>())
        }

        private fun SearchScreenState.assertArtifactsAndExceptionsNotEmpty() {
            assertArtifactsNotEmpty()
            assertThat(exceptions).isNotEqualTo(emptyList<PoodleException>())
            assertThat(shouldShowArtifactsAndExceptions).isEqualTo(true)
        }

        private fun SearchScreenState.assertArtifactsAndExceptionsEmpty() {
            assertThat(shouldShowArtifacts).isEqualTo(false)
            assertThat(shouldShowExceptions).isEqualTo(false)
            assertThat(shouldShowArtifactsAndExceptions).isEqualTo(false)
            assertThat(artifacts).isEqualTo(emptyList<Artifact>())
            assertThat(exceptions).isEqualTo(emptyList<PoodleException>())
        }
    }
}

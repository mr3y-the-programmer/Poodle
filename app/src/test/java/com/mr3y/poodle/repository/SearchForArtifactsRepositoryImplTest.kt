package com.mr3y.poodle.repository

import app.cash.turbine.FlowTurbine
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.google.testing.junit.testparameterinjector.TestParameter
import com.google.testing.junit.testparameterinjector.TestParameterInjector
import com.mr3y.poodle.network.datasources.FakeJitPackImpl
import com.mr3y.poodle.network.datasources.FakeMavenCentralImpl
import com.mr3y.poodle.network.models.Result
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(TestParameterInjector::class)
class SearchForArtifactsRepositoryImplTest {

    @TestParameter val isSearchOnMavenEnabled = false
    @TestParameter val isSearchOnJitPackEnabled = false

    @Test
    fun `given a fake search query & a combination of parameters, then it should return matched artifacts from enabled data sources`() = runTest {
        sut.searchByQuery(fakeSearchQuery, isSearchOnMavenEnabled, isSearchOnJitPackEnabled).test {
            if (isSearchOnMavenEnabled) {
                awaitArtifactsFromSource(Source.MavenCentral) { artifacts ->
                    assertThat(artifacts).isEqualTo(Result.Success(fakeArtifactsPart1))
                }
            }
            if (isSearchOnJitPackEnabled) {
                awaitArtifactsFromSource(Source.JitPack) { artifacts ->
                    assertThat(artifacts).isEqualTo(Result.Success(fakeArtifactsPart2))
                }
            }
            awaitComplete()
        }
    }

    private suspend inline fun FlowTurbine<SearchResult>.awaitArtifactsFromSource(source: Source, onArtifactsReceived: (Result<List<Artifact>>) -> Unit) {
        var nextItem = awaitItem()
        assertThat(nextItem.first).isEqualTo(Result.Loading)
        assertThat(nextItem.second).isEqualTo(source)
        nextItem = awaitItem()
        onArtifactsReceived(nextItem.first)
        assertThat(nextItem.second).isEqualTo(source)
    }

    companion object {

        private lateinit var sut: SearchForArtifactsRepositoryImpl

        @BeforeClass
        @JvmStatic
        fun setup() {
            sut = SearchForArtifactsRepositoryImpl(FakeMavenCentralImpl(), FakeJitPackImpl())
        }
    }
}

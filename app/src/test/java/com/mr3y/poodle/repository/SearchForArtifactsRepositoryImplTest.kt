package com.mr3y.poodle.repository

import app.cash.turbine.FlowTurbine
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.google.testing.junit.testparameterinjector.TestParameterInjector
import com.google.testing.junit.testparameterinjector.TestParameters
import com.mr3y.poodle.network.datasources.FakeJitPackImpl
import com.mr3y.poodle.network.datasources.FakeMavenCentralImpl
import com.mr3y.poodle.network.models.Result
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(TestParameterInjector::class)
class SearchForArtifactsRepositoryImplTest {

    @Test
    fun `given both data sources are disabled, or search query isn't sufficient, then verify it returns null`() = runTest {
        var searchQuery = SearchQuery("compos", "", 0, "", emptySet(), "", "")

        sut.searchByQuery(searchQuery, searchOnMaven = false, searchOnJitPack = false).test {
            assertThat(awaitItem()).isNull()
            awaitComplete()
        }
        searchQuery = SearchQuery("c", "", 0, "", emptySet(), "", "")
        sut.searchByQuery(searchQuery, searchOnMaven = true, searchOnJitPack = true).test {
            assertThat(awaitItem()).isNull()
            awaitComplete()
        }
    }

    @Test
    @TestParameters("{isSearchOnMavenEnabled: true, isSearchOnJitPackEnabled: true}")
    @TestParameters("{isSearchOnMavenEnabled: false, isSearchOnJitPackEnabled: true}")
    @TestParameters("{isSearchOnMavenEnabled: true, isSearchOnJitPackEnabled: false}")
    fun `given a fake search query, and at least one of the two data sources is enabled, then it should return new result`(
        isSearchOnMavenEnabled: Boolean,
        isSearchOnJitPackEnabled: Boolean
    ) = runTest {
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

    @Test
    fun `given a cached search query after a successful data loading, then verify we get a cached result`() = runTest {
        val searchQuery = SearchQuery("compos", "", 0, "", emptySet(), "", "")
        val isSearchOnMavenCentralEnabled = true
        val isSearchOnJitpackEnabled = true

        // simulate searching for the first time to cache the result
        launch {
            sut.searchByQuery(
                searchQuery,
                isSearchOnMavenCentralEnabled,
                isSearchOnJitpackEnabled
            ).collect()
        }.join()

        sut.searchByQuery(
            searchQuery,
            !isSearchOnMavenCentralEnabled,
            isSearchOnJitpackEnabled
        ).test {
            assertThat(awaitItem()).isEqualTo(SearchResult(first = Result.Success(fakeArtifactsPart2), second = Source.JitPack))
            awaitComplete()
        }

        sut.searchByQuery(
            searchQuery,
            isSearchOnMavenCentralEnabled,
            !isSearchOnJitpackEnabled
        ).test {
            assertThat(awaitItem()).isEqualTo(SearchResult(first = Result.Success(fakeArtifactsPart1), second = Source.MavenCentral))
            awaitComplete()
        }
    }

    private suspend inline fun FlowTurbine<SearchResult?>.awaitArtifactsFromSource(source: Source, onArtifactsReceived: (Result<List<Artifact>>) -> Unit) {
        var nextItem = awaitItem()
        assertThat(nextItem!!.first).isEqualTo(Result.Loading)
        assertThat(nextItem.second).isEqualTo(source)
        nextItem = awaitItem()
        onArtifactsReceived(nextItem!!.first)
        assertThat(nextItem.second).isEqualTo(source)
    }

    @After
    fun teardown() {
        // clear caches between tests
        sut.cachedSearchQuery = null
        sut.mavenCentralCachedSearchResult = null
        sut.jitPackCachedSearchResult = null
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

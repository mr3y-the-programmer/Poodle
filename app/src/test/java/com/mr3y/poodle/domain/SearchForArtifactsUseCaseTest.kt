package com.mr3y.poodle.domain

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.mr3y.poodle.network.models.Result
import com.mr3y.poodle.repository.FakeSearchForArtifactsRepository
import com.mr3y.poodle.repository.SearchQuery
import com.mr3y.poodle.repository.SearchResult
import com.mr3y.poodle.repository.Source
import com.mr3y.poodle.repository.fakeArtifactsPart2
import com.mr3y.poodle.utils.TestDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SearchForArtifactsUseCaseTest {

    @get:Rule
    val testDispatcherRule = TestDispatcherRule()

    private lateinit var searchForArtifacts: SearchForArtifactsUseCase

    @Before
    fun setUp() {
        searchForArtifacts = SearchForArtifactsUseCase(FakeSearchForArtifactsRepository(fakeSearchResults), testDispatcherRule.testDispatcher)
    }

    @Test
    fun `given both data sources are disabled, or search query isn't sufficient, then verify it returns null`() = runTest {
        var searchQuery = SearchQuery("compos", "", null, "", emptySet(), "", "")
        var isSearchOnMavenCentralEnabled = false
        var isSearchOnJitpackEnabled = false
        searchForArtifacts(searchQuery, isSearchOnMavenCentralEnabled, isSearchOnJitpackEnabled).test {
            assertThat(awaitItem()).isNull()
            awaitComplete()
        }
        searchQuery = SearchQuery("c", "", null, "", emptySet(), "", "")
        isSearchOnMavenCentralEnabled = true
        isSearchOnJitpackEnabled = true
        searchForArtifacts(searchQuery, isSearchOnMavenCentralEnabled, isSearchOnJitpackEnabled).test {
            assertThat(awaitItem()).isNull()
            awaitComplete()
        }
    }

    @Test
    fun `given a valid search query, and either data source is enabled, then verify we get a new search result & update the cache`() = runTest {
        val searchQuery = SearchQuery("compos", "", null, "", emptySet(), "", "")
        val isSearchOnMavenCentralEnabled = true
        val isSearchOnJitpackEnabled = true
        searchForArtifacts(searchQuery, isSearchOnMavenCentralEnabled, isSearchOnJitpackEnabled).test {
            fakeSearchResults.collect {
                assertThat(awaitItem()).isEqualTo(it)
            }
            awaitComplete()
            assertThat(searchForArtifacts.cachedSearchQuery).isNotNull()
            assertThat(searchForArtifacts.mavenCentralCachedSearchResult).isNotNull()
            assertThat(searchForArtifacts.jitPackCachedSearchResult).isNotNull()
        }
    }

    @Test
    fun `given a cached search query after a successful data loading, then verify we get a cached result`() = runTest {
        val searchQuery = SearchQuery("compos", "", null, "", emptySet(), "", "")
        val isSearchOnMavenCentralEnabled = true
        val isSearchOnJitpackEnabled = true

        // simulate searching for the first time to cache the result
        launch {
            searchForArtifacts(
                searchQuery,
                isSearchOnMavenCentralEnabled,
                isSearchOnJitpackEnabled
            ).collect()
        }.join()

        searchForArtifacts(
            searchQuery,
            !isSearchOnMavenCentralEnabled,
            isSearchOnJitpackEnabled
        ).test {
            assertThat(awaitItem()).isEqualTo(SearchResult(first = Result.Success(fakeArtifactsPart2), second = Source.JitPack))
            awaitComplete()
        }
    }
}

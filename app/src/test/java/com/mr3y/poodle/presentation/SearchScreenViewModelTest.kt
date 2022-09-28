package com.mr3y.poodle.presentation

import com.google.common.truth.Truth.assertThat
import com.mr3y.poodle.domain.SearchForArtifactsUseCase
import com.mr3y.poodle.domain.SearchUiState
import com.mr3y.poodle.domain.fakeSearchResults
import com.mr3y.poodle.network.models.Result
import com.mr3y.poodle.repository.Artifact
import com.mr3y.poodle.repository.SearchResult
import com.mr3y.poodle.repository.Source
import com.mr3y.poodle.repository.fixtures.FakeSearchForArtifactsRepository
import com.mr3y.poodle.utils.TestDispatcherRule
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import java.time.ZonedDateTime

class SearchScreenViewModelTest {

    @get:Rule
    val testDispatcherRule = TestDispatcherRule()

    @Test
    fun `given an initial state, when we try to reduce it a number of times, then make sure every reduced state is a valid state`() {
        val state0 = SearchUiState.Initial

        val state1 = viewModel.reduce(state0, SearchResult(Result.Loading, Source.MavenCentral))
        assertThat(state1).isEqualTo(SearchUiState(Result.Loading, null))

        val state2 = viewModel.reduce(state1, SearchResult(Result.Loading, Source.JitPack))
        assertThat(state2).isEqualTo(SearchUiState(Result.Loading, Result.Loading))

        val artifacts = listOf(Artifact.MavenCentralArtifact("mavenCentral", "1.7", "aar", ZonedDateTime.now()))
        val state3 = viewModel.reduce(state2, SearchResult(Result.Success(artifacts), Source.MavenCentral))
        assertThat(state3).isEqualTo(SearchUiState(Result.Success(artifacts), Result.Loading))

        val state4 = viewModel.reduce(state3, SearchResult(Result.Error(), Source.JitPack))
        assertThat(state4).isEqualTo(
            SearchUiState(
                Result.Success(artifacts),
                Result.Error()
            )
        )

        val state5 = viewModel.reduce(
            state4,
            SearchResult(Result.Loading, Source.JitPack),
            isSearchingOnMavenEnabled = false
        )
        assertThat(state5).isEqualTo(SearchUiState(null, Result.Loading))

        val state6 = viewModel.reduce(
            state4,
            SearchResult(Result.Loading, Source.MavenCentral),
            isSearchingOnJitPackEnabled = false
        )
        assertThat(state6).isEqualTo(SearchUiState(Result.Loading, null))

        val state7 = viewModel.reduce(
            state4,
            null
        )
        assertThat(state7).isEqualTo(SearchUiState.Initial)
    }

    companion object {

        private lateinit var viewModel: SearchScreenViewModel

        @BeforeClass
        @JvmStatic
        fun setUp() {
            viewModel = SearchScreenViewModel(
                SearchForArtifactsUseCase(FakeSearchForArtifactsRepository(fakeSearchResults))
            )
        }
    }
}

package com.mr3y.poodle.domain

import com.google.common.truth.Truth.assertThat
import com.mr3y.poodle.network.models.Result
import com.mr3y.poodle.repository.Artifact
import com.mr3y.poodle.repository.SearchResult
import com.mr3y.poodle.repository.Source
import org.junit.Test
import java.time.ZonedDateTime

class SearchUiStateReducingTest {
    @Test
    fun `given an initial state, when we try to reduce it a number of times, then make sure every reduced state is a valid state`() {
        val state0 = SearchUiState.Initial

        val state1 = state0.reduce(SearchResult(Result.Loading, Source.MavenCentral))
        assertThat(state1).isEqualTo(SearchUiState(Result.Loading, null))

        val state2 = state1.reduce(SearchResult(Result.Loading, Source.JitPack))
        assertThat(state2).isEqualTo(SearchUiState(Result.Loading, Result.Loading))

        val artifacts =
            listOf(Artifact.MavenCentralArtifact("mavenCentral", "1.7", "aar", ZonedDateTime.now()))
        val state3 = state2.reduce(SearchResult(Result.Success(artifacts), Source.MavenCentral))
        assertThat(state3).isEqualTo(SearchUiState(Result.Success(artifacts), Result.Loading))

        val state4 =
            state3.reduce(SearchResult(Result.Error(), Source.JitPack))
        assertThat(state4).isEqualTo(
            SearchUiState(
                Result.Success(artifacts),
                Result.Error()
            )
        )

        val state5 = state4.reduce(
            SearchResult(Result.Loading, Source.JitPack),
            isSearchingOnMavenEnabled = false
        )
        assertThat(state5).isEqualTo(SearchUiState(null, Result.Loading))

        val state6 = state4.reduce(
            SearchResult(Result.Loading, Source.MavenCentral),
            isSearchingOnJitPackEnabled = false
        )
        assertThat(state6).isEqualTo(SearchUiState(Result.Loading, null))
    }
}

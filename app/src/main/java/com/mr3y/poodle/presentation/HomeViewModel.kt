package com.mr3y.poodle.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mr3y.poodle.network.exceptions.PoodleException
import com.mr3y.poodle.network.models.Result
import com.mr3y.poodle.repository.Artifact
import com.mr3y.poodle.repository.SearchForArtifactsRepository
import com.mr3y.poodle.repository.SearchQuery
import javax.inject.Inject

@OptIn(ExperimentalLifecycleComposeApi::class)
class HomeViewModel @Inject constructor(
    private val searchForArtifactsRepository: SearchForArtifactsRepository
) : ViewModel() {

    var searchQuery by mutableStateOf(SearchQuery.EMPTY)
        private set

    var isSearchOnMavenEnabled by mutableStateOf(true)
        private set

    var isSearchOnJitPackEnabled by mutableStateOf(true)
        private set

    private var initialState by mutableStateOf(HomeScreenState.Initial)

    val homeState: State<Result<List<Artifact>>>
        @Composable
        get() = searchForArtifactsRepository.searchByQuery(searchQuery, isSearchOnMavenEnabled, isSearchOnJitPackEnabled)
            .collectAsStateWithLifecycle(initialValue = Result.Loading)

    data class HomeScreenState(
        val areMavenCentralArtifactsLoading: Boolean,
        val areJitPackArtifactsLoading: Boolean,
        val mavenCentralArtifacts: List<Artifact>? = null,
        val jitpackArtifacts: List<Artifact>? = null,
        val onMavenCentralException: PoodleException? = null,
        val onJitPackException: PoodleException? = null
    ) {
        companion object {
            val Initial = HomeScreenState(areMavenCentralArtifactsLoading = false, areJitPackArtifactsLoading = false)
        }
    }
}

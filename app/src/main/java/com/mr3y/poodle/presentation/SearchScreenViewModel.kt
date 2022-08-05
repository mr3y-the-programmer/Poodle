package com.mr3y.poodle.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.snapshots.Snapshot
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mr3y.poodle.network.models.Result
import com.mr3y.poodle.network.models.data
import com.mr3y.poodle.network.models.exception
import com.mr3y.poodle.repository.Artifact
import com.mr3y.poodle.repository.SearchForArtifactsRepository
import com.mr3y.poodle.repository.SearchQuery
import com.mr3y.poodle.repository.SearchResult
import com.mr3y.poodle.repository.Source
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

private const val SEARCH_QUERY_DEBOUNCE_THRESHOLD = 600L

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
class SearchScreenViewModel @Inject constructor(
    private val searchForArtifactsRepository: SearchForArtifactsRepository,
) : ViewModel() {

    var searchQuery = mutableStateOf(SearchQuery.EMPTY)
        private set

    var isSearchOnMavenEnabled = mutableStateOf(true)
        private set

    var isSearchOnJitPackEnabled = mutableStateOf(true)
        private set

    private var internalState by mutableStateOf(SearchScreenState.Initial)

    val homeState: StateFlow<SearchScreenState> = combine(
        snapshotFlow { searchQuery.value }.debounce(SEARCH_QUERY_DEBOUNCE_THRESHOLD),
        snapshotFlow { isSearchOnMavenEnabled.value },
        snapshotFlow { isSearchOnJitPackEnabled.value }
    ) { query, enableSearchingOnMaven, enableSearchingOnJitPack ->
        if (query == SearchQuery.EMPTY || query.text.length < 2 || (!enableSearchingOnMaven && !enableSearchingOnJitPack)) {
            return@combine flowOf(SearchScreenState.Initial)
        }
        internalState = internalState.reInitializeState(enableSearchingOnMaven, enableSearchingOnJitPack)
        searchForArtifactsRepository.searchByQuery(
            query,
            enableSearchingOnMaven,
            enableSearchingOnJitPack
        ).map { searchResult ->
            Snapshot.withMutableSnapshot {
                internalState = internalState.reduce(searchResult, enableSearchingOnMaven, enableSearchingOnJitPack)
            }
            internalState
        }
    }
        .flatMapLatest { it }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
            initialValue = internalState
        )

    fun toggleSearchOnMaven(enabled: Boolean) {
        Snapshot.withMutableSnapshot {
            isSearchOnMavenEnabled.value = enabled
        }
    }

    fun toggleSearchOnJitPack(enabled: Boolean) {
        Snapshot.withMutableSnapshot {
            isSearchOnJitPackEnabled.value = enabled
        }
    }

    fun updateSearchQuery(
        searchText: String? = null,
        groupId: String? = null,
        limit: Int? = null,
        packaging: String? = null,
        tags: Set<String>? = null,
        containsClassSimpleName: String? = null,
        containsClassFQN: String? = null
    ) {
        Snapshot.withMutableSnapshot {
            searchQuery.value = searchQuery.value.copy(
                text = searchText ?: searchQuery.value.text,
                groupId = groupId ?: searchQuery.value.groupId,
                limit = limit ?: searchQuery.value.limit,
                packaging = packaging ?: searchQuery.value.packaging,
                tags = tags ?: searchQuery.value.tags,
                containsClassSimpleName = containsClassSimpleName ?: searchQuery.value.containsClassSimpleName,
                containsClassFullyQualifiedName = containsClassFQN ?: searchQuery.value.containsClassFullyQualifiedName
            )
        }
    }
}

data class SearchScreenState(
    private val mavenCentralArtifacts: Result<List<Artifact>>? = null,
    private val jitPackArtifacts: Result<List<Artifact>>? = null,
) {
    val isLoading = mavenCentralArtifacts.isLoading() || jitPackArtifacts.isLoading()
    val artifacts = listOfNotNull(mavenCentralArtifacts?.data, jitPackArtifacts?.data).flatten()
    val exceptions = listOfNotNull(mavenCentralArtifacts?.exception, jitPackArtifacts?.exception)
    val isIdle: Boolean
        get() = !isLoading && mavenCentralArtifacts == null && jitPackArtifacts == null

    val shouldShowArtifacts: Boolean
        get() = !isLoading && (!mavenCentralArtifacts.isError() && !jitPackArtifacts.isError()) && (mavenCentralArtifacts.isSuccess() || jitPackArtifacts.isSuccess())

    val shouldShowExceptions: Boolean
        get() = !isLoading && (!mavenCentralArtifacts.isSuccess() && !jitPackArtifacts.isSuccess()) && (mavenCentralArtifacts.isError() || jitPackArtifacts.isError())

    val shouldShowArtifactsAndExceptions: Boolean
        get() = !isLoading && (mavenCentralArtifacts.isSuccess() xor jitPackArtifacts.isSuccess()) && (mavenCentralArtifacts.isError() xor jitPackArtifacts.isError())

    fun reduce(
        searchResult: SearchResult,
        isSearchingOnMavenEnabled: Boolean = true,
        isSearchingOnJitPackEnabled: Boolean = true
    ): SearchScreenState {
        val (artifacts, source) = searchResult
        val mavenCentralArtifacts = when {
            isSearchingOnMavenEnabled && source == Source.MavenCentral -> artifacts
            isSearchingOnMavenEnabled -> this.mavenCentralArtifacts
            else -> null
        }
        val jitPackArtifacts = when {
            isSearchingOnJitPackEnabled && source == Source.JitPack -> artifacts
            isSearchingOnJitPackEnabled -> this.jitPackArtifacts
            else -> null
        }
        return SearchScreenState(
            mavenCentralArtifacts = mavenCentralArtifacts,
            jitPackArtifacts = jitPackArtifacts
        )
    }

    fun reInitializeState(
        isSearchingOnMavenEnabled: Boolean,
        isSearchingOnJitPackEnabled: Boolean
    ): SearchScreenState {
        return when {
            !isSearchingOnMavenEnabled -> SearchScreenState(mavenCentralArtifacts = null, jitPackArtifacts = this.jitPackArtifacts)
            !isSearchingOnJitPackEnabled -> SearchScreenState(mavenCentralArtifacts = this.mavenCentralArtifacts, jitPackArtifacts = null)
            else -> SearchScreenState(Result.Loading, Result.Loading)
        }
    }

    private fun <T> Result<T>?.isLoading() = this is Result.Loading

    private fun <T> Result<T>?.isSuccess() = this is Result.Success

    private fun <T> Result<T>?.isError() = this is Result.Error

    companion object {
        val Initial = SearchScreenState()
    }
}

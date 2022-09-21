package com.mr3y.poodle.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.snapshots.Snapshot
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mr3y.poodle.domain.SearchForArtifactsUseCase
import com.mr3y.poodle.domain.SearchUiState
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
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

private const val SEARCH_QUERY_DEBOUNCE_THRESHOLD = 600L

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
class SearchScreenViewModel @Inject constructor(
    private val searchForArtifactsUseCase: SearchForArtifactsUseCase
) : ViewModel() {

    var searchQuery = mutableStateOf(SearchQuery.EMPTY)
        private set

    var isSearchOnMavenEnabled = mutableStateOf(true)
        private set

    var isSearchOnJitPackEnabled = mutableStateOf(true)
        private set

    private var internalState by mutableStateOf(SearchUiState.Initial)

    val homeState: StateFlow<SearchUiState> = combine(
        snapshotFlow { searchQuery.value }.debounce(SEARCH_QUERY_DEBOUNCE_THRESHOLD),
        snapshotFlow { isSearchOnMavenEnabled.value },
        snapshotFlow { isSearchOnJitPackEnabled.value }
    ) { query, enableSearchingOnMaven, enableSearchingOnJitPack ->
        searchForArtifactsUseCase(
            query,
            enableSearchingOnMaven,
            enableSearchingOnJitPack
        ).map { searchResult ->
            Snapshot.withMutableSnapshot {
                internalState = reduce(
                    internalState,
                    searchResult,
                    enableSearchingOnMaven,
                    enableSearchingOnJitPack
                )
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
        val isAllNull = searchText == null && groupId == null && limit == null &&
            packaging == null && tags == null && containsClassSimpleName == null && containsClassFQN == null
        Snapshot.withMutableSnapshot {
            searchQuery.value = searchQuery.value.copy(
                text = searchText ?: searchQuery.value.text,
                groupId = groupId ?: searchQuery.value.groupId,
                limit = if (limit != null || isAllNull) limit else searchQuery.value.limit,
                packaging = packaging ?: searchQuery.value.packaging,
                tags = tags ?: searchQuery.value.tags,
                containsClassSimpleName = containsClassSimpleName ?: searchQuery.value.containsClassSimpleName,
                containsClassFullyQualifiedName = containsClassFQN ?: searchQuery.value.containsClassFullyQualifiedName
            )
        }
    }

    internal fun reduce(
        previousState: SearchUiState,
        searchResult: SearchResult?,
        isSearchingOnMavenEnabled: Boolean = true,
        isSearchingOnJitPackEnabled: Boolean = true
    ): SearchUiState {
        if (searchResult == null) return SearchUiState.Initial

        val (artifacts, source) = searchResult
        val mavenCentralArtifacts = when {
            isSearchingOnMavenEnabled && source == Source.MavenCentral -> artifacts
            isSearchingOnMavenEnabled -> previousState.mavenCentralArtifacts
            else -> null
        }
        val jitPackArtifacts = when {
            isSearchingOnJitPackEnabled && source == Source.JitPack -> artifacts
            isSearchingOnJitPackEnabled -> previousState.jitPackArtifacts
            else -> null
        }
        return SearchUiState(
            mavenCentralArtifacts = mavenCentralArtifacts,
            jitPackArtifacts = jitPackArtifacts
        )
    }
}

package com.mr3y.poodle.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Scaffold
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mr3y.poodle.R
import com.mr3y.poodle.domain.SearchUiState
import com.mr3y.poodle.network.models.Result
import com.mr3y.poodle.presentation.SearchScreenViewModel
import com.mr3y.poodle.repository.Artifact
import com.mr3y.poodle.repository.SearchQuery
import com.mr3y.poodle.ui.components.AnimatedTopAppBar
import com.mr3y.poodle.ui.components.ArtworkWithText
import com.mr3y.poodle.ui.components.FiltersBottomSheet
import com.mr3y.poodle.ui.components.FiltersState
import com.mr3y.poodle.ui.components.TextChip
import com.mr3y.poodle.ui.preview_utils.MultiThemePreview
import com.mr3y.poodle.ui.theme.PoodleTheme
import io.github.aakira.napier.Napier
import kotlinx.coroutines.launch
import java.time.ZonedDateTime

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun SearchDependenciesScreen(viewModel: SearchScreenViewModel = viewModel()) {
    val homeState by viewModel.homeState.collectAsStateWithLifecycle(initialValue = SearchUiState.Initial)
    val query by remember { viewModel.searchQuery }
    val filters by remember {
        derivedStateOf {
            FiltersState(
                viewModel.isSearchOnMavenEnabled.value,
                viewModel::toggleSearchOnMaven,
                viewModel.isSearchOnJitPackEnabled.value,
                viewModel::toggleSearchOnJitPack,
                query.groupId,
                { viewModel.updateSearchQuery(groupId = it) },
                query.packaging,
                { viewModel.updateSearchQuery(packaging = it) },
                query.tags,
                { viewModel.updateSearchQuery(tags = it) },
                query.limit,
                { viewModel.updateSearchQuery(limit = it) },
                query.containsClassSimpleName,
                { viewModel.updateSearchQuery(containsClassSimpleName = it) },
                query.containsClassFullyQualifiedName,
                { viewModel.updateSearchQuery(containsClassFQN = it) }
            )
        }
    }
    SearchDependencies(homeState, query, { viewModel.updateSearchQuery(searchText = it) }, filters)
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
internal fun SearchDependencies(
    state: SearchUiState,
    searchQuery: SearchQuery,
    onSearchQueryTextChanged: (String) -> Unit,
    filtersState: FiltersState
) {
    val bottomSheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    val scope = rememberCoroutineScope()
    val rootInteractionSource = remember { MutableInteractionSource() }
    ModalBottomSheetLayout(
        sheetState = bottomSheetState,
        modifier = Modifier
            .padding(
                WindowInsets.safeDrawing
                    .only(WindowInsetsSides.Horizontal)
                    .asPaddingValues()
            )
            .fillMaxSize()
            .clickable(
                interactionSource = rootInteractionSource,
                indication = null,
                onClick = {}
            ),
        sheetContent = {
            FiltersBottomSheet(filtersState)
        }
    ) {
        val scaffoldState = rememberScaffoldState()
        Scaffold(
            scaffoldState = scaffoldState,
            modifier = Modifier.windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Vertical)),
            topBar = {
                AnimatedTopAppBar(
                    searchQuery.text,
                    onSearchQueryTextChanged,
                    isFilteringEnabled = state != SearchUiState.Initial,
                    onFilterItemsClicked = { scope.launch { bottomSheetState.show() } },
                    rootInteractionSource = rootInteractionSource
                )
            }
        ) { contentPadding ->
            if (state == SearchUiState.Initial) {
                Initial(
                    modifier = Modifier
                        .padding(contentPadding)
                        .fillMaxSize()
                )
            } else {
                val selectedTabIndex = rememberSaveable(Unit) { mutableStateOf(0) }
                val tabRowHeight = 56.dp
                TabRow(
                    selectedTabIndex = selectedTabIndex.value,
                    onSelectingNewTab = { selectedTabIndex.value = it },
                    uiState = state,
                    modifier = Modifier
                        .padding(top = contentPadding.calculateTopPadding())
                        .height(tabRowHeight)
                )
                TabContent(
                    state.getArtifactsBasedOnIndex(selectedTabIndex.value),
                    modifier = Modifier
                        .padding(contentPadding)
                        .padding(top = tabRowHeight)
                        .fillMaxSize()
                )
            }
        }
    }
}

enum class TabLabel {
    MavenCentral,

    JitPack
}

@Composable
fun TabRow(
    selectedTabIndex: Int,
    onSelectingNewTab: (index: Int) -> Unit,
    uiState: SearchUiState,
    modifier: Modifier = Modifier
) {
    val exactlyOneTabExists = (uiState.mavenCentralArtifacts == null) xor (uiState.jitPackArtifacts == null)
    TabRow(
        selectedTabIndex = if (exactlyOneTabExists) 0 else selectedTabIndex,
        modifier = modifier,
        backgroundColor = MaterialTheme.colors.surface,
        contentColor = MaterialTheme.colors.primary,
    ) {
        if (exactlyOneTabExists) {
            Tab(selected = true, onClick = { }) {
                val label = if (uiState.mavenCentralArtifacts != null) "MavenCentral" else "JitPack"
                Text(text = label)
            }
        } else {
            for ((index, tabLabel) in TabLabel.values().withIndex()) {
                Tab(
                    selected = index == selectedTabIndex,
                    onClick = { onSelectingNewTab(index) }
                ) {
                    Text(text = tabLabel.name)
                }
            }
        }
    }
}

@Composable
fun TabContent(
    content: Result<List<Artifact>>,
    modifier: Modifier = Modifier
) {
    when (content) {
        is Result.Loading -> {
            Box(
                modifier = modifier.testTag("LoadingIndicator"),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(48.dp)
                )
            }
        }
        is Result.Success -> {
            val artifacts = content.data
            if (artifacts.isEmpty()) {
                Empty(modifier = modifier)
            } else {
                DisplaySearchResults(artifacts = artifacts, modifier = modifier)
            }
        }
        is Result.Error -> {
            Error(modifier = modifier)
        }
    }
}

@Composable
private fun Initial(modifier: Modifier = Modifier) {
    ArtworkWithText(
        drawableResId = if (isSystemInDarkTheme()) R.drawable.initial_vector_dark else R.drawable.initial_vector_light,
        text = "Search for any artifacts On MavenCentral, Or JitPack",
        modifier = modifier
    )
}

@Composable
private fun Empty(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "No results were found matching your search query criteria. refine your search, and try again",
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun DisplaySearchResults(artifacts: List<Artifact>, modifier: Modifier = Modifier) {
    val pagesState = rememberSearchResultsListState(artifacts = artifacts)
    key(artifacts) {
        Box(
            modifier = modifier.imePadding()
        ) {
            val listState = rememberLazyListState()
            val scope = rememberCoroutineScope()
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                state = listState,
            ) {
                if (pagesState.totalNumOfAllMatchedArtifacts > pagesState.numOfArtifactsPerPage) {
                    item {
                        Column(
                            modifier = Modifier
                                .background(MaterialTheme.colors.primaryVariant.copy(alpha = 0.35f))
                                .fillMaxWidth()
                                .padding(top = 8.dp, start = 8.dp, end = 8.dp)
                        ) {
                            Text(text = "Found ${pagesState.totalNumOfAllMatchedArtifacts} artifacts that matches your search")
                            Row(
                                modifier = Modifier
                                    .padding(vertical = 8.dp)
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "Displaying artifacts: ${pagesState.currentPage.first} - ${pagesState.currentPage.last}")
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    IconButton(
                                        modifier = Modifier
                                            .size(64.dp)
                                            .clip(CircleShape)
                                            .semantics { },
                                        onClick = pagesState::backToThePreviousPage,
                                        enabled = !pagesState.isFirstPage
                                    ) {
                                        Icon(
                                            painter = rememberVectorPainter(image = Icons.Filled.KeyboardArrowLeft),
                                            contentDescription = null,
                                            modifier = Modifier.size(32.dp)
                                        )
                                    }
                                    IconButton(
                                        modifier = Modifier
                                            .size(64.dp)
                                            .clip(CircleShape)
                                            .semantics { },
                                        onClick = pagesState::goToNextPage,
                                        enabled = !pagesState.isLastPage
                                    ) {
                                        Icon(
                                            painter = rememberVectorPainter(image = Icons.Filled.KeyboardArrowRight),
                                            contentDescription = null,
                                            modifier = Modifier.size(32.dp)
                                        )
                                    }
                                }
                            }
                        }
                        Divider()
                    }
                }
                items(pagesState.getCurrentPageArtifactsOf(artifacts)) { artifact ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        val gav = buildString {
                            append(artifact.fullId)
                            if (artifact.latestVersion != null) {
                                append(":")
                                append(artifact.latestVersion)
                            }
                        }
                        Text(
                            text = gav,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = if (artifact is Artifact.JitPackArtifact) Arrangement.End else Arrangement.SpaceBetween
                        ) {
                            if (artifact is Artifact.MavenCentralArtifact) {
                                TextChip(
                                    text = artifact.packaging,
                                    Modifier
                                        .wrapContentHeight()
                                        .widthIn(min = 48.dp, max = 120.dp)
                                )
                            }
                            TextChip(text = "Updated: ${artifact.lastUpdated?.toLocalDate() ?: "N/A"}")
                        }
                        Divider()
                    }
                }
            }
            val isScrollToTopButtonVisible by remember { derivedStateOf { listState.firstVisibleItemIndex > 1 } }
            AnimatedVisibility(
                visible = isScrollToTopButtonVisible,
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.BottomEnd)
                    .size(56.dp),
                enter = fadeIn() + slideInVertically { it },
                exit = fadeOut() + slideOutVertically { it }
            ) {
                IconButton(
                    onClick = { scope.launch { listState.animateScrollToItem(0) } },
                    modifier = Modifier
                        .shadow(16.dp, shape = CircleShape)
                        .clip(CircleShape)
                        .background(MaterialTheme.colors.secondary)
                        .fillMaxSize()
                ) {
                    Icon(
                        painter = rememberVectorPainter(image = Icons.Filled.ArrowUpward),
                        contentDescription = "Scroll to the top",
                        tint = MaterialTheme.colors.onSecondary
                    )
                }
            }
        }
    }
}

@Composable
private fun Error(modifier: Modifier = Modifier) {
    val message = buildString {
        append("Oops!, things didn't go well while trying to satisfy your search request.")
    }
    ArtworkWithText(
        drawableResId = if (isSystemInDarkTheme()) R.drawable.error_dark else R.drawable.error_light,
        text = message,
        modifier
    )
}

private fun SearchUiState.getArtifactsBasedOnIndex(selectedTabIndex: Int): Result<List<Artifact>> {
    return when {
        mavenCentralArtifacts != null && jitPackArtifacts != null -> {
            if (selectedTabIndex == 0) mavenCentralArtifacts else jitPackArtifacts
        }
        mavenCentralArtifacts == null && jitPackArtifacts != null -> jitPackArtifacts
        mavenCentralArtifacts != null && jitPackArtifacts == null -> mavenCentralArtifacts
        else -> {
            val message = "Tabs shouldn't be visible if UiState == Initial"
            Napier.wtf(message)
            throw IllegalStateException(message)
        }
    }
}

@Composable
@MultiThemePreview
fun SearchDependenciesInitialPreview() {
    PoodleTheme {
        SearchDependencies(
            SearchUiState.Initial,
            SearchQuery.EMPTY,
            {},
            FiltersState.Default
        )
    }
}

@Composable
@MultiThemePreview
fun SearchDependenciesSuccessPreview() {
    val mavenCentralArtifacts = Result.Success(
        listOf(
            Artifact.MavenCentralArtifact("com.juul.krayon:compose", "0.13.0-alpha4", "jar", ZonedDateTime.now()),
            Artifact.MavenCentralArtifact("com.patrykmichalik.opto:compose", "1.0.16", "aar", ZonedDateTime.now()),
            Artifact.MavenCentralArtifact("com.keyri:compose", "1.4.2", "aar", ZonedDateTime.now()),
            Artifact.MavenCentralArtifact("io.github.kakaocup:compose", "0.1.0", "aar", ZonedDateTime.now()),
            Artifact.MavenCentralArtifact("com.patrykandpatryk.vico:compose", "1.4.3", "aar", ZonedDateTime.now()),
            Artifact.MavenCentralArtifact("com.freeletics.flowredux:compose", "1.0.0", "pom", ZonedDateTime.now()),
            Artifact.MavenCentralArtifact("net.meilcli.rippletext:compose", "0.0.1", "aar", ZonedDateTime.now()),
            Artifact.MavenCentralArtifact("io.github.woody230.ktx:compose", "4.1.0", "jar", ZonedDateTime.now()),
            Artifact.MavenCentralArtifact("com.tunjid.tiler:compose", "0.0.3", "jar", ZonedDateTime.now()),
            Artifact.MavenCentralArtifact("com.qmuiteam:compose", "1.1.1", "aar", ZonedDateTime.now()),
        )
    )
    val jitPackArtifacts = Result.Success(
        listOf(
            Artifact.JitPackArtifact("com.github.masayukisuda:mp4composer-android", "0.1.0", ZonedDateTime.now()),
            Artifact.JitPackArtifact("com.github.jeziellago:compose-markdown", "2.1.0", null),
            Artifact.JitPackArtifact("com.github.takuji31:navigation-compose-screen", null, ZonedDateTime.now()),
            Artifact.JitPackArtifact("com.github.trevjonez:composer-gradle-plugin", "1.3.4", ZonedDateTime.now()),
            Artifact.JitPackArtifact("com.github.ireward:compose-html", null, null),
            Artifact.JitPackArtifact("com.github.sahabpardaz:docker-compose-wrapper", "1.1.2", ZonedDateTime.now()),
            Artifact.JitPackArtifact("com.github.fornewid:material-motion-compose", "0.13.0-alpha4", null),
            Artifact.JitPackArtifact("com.github.zsoltk:compose-router", "0.15.0-snapshot", ZonedDateTime.now()),
            Artifact.JitPackArtifact("com.github.takahirom:groupie-compose-item", "2.13.0", ZonedDateTime.now()),
            Artifact.JitPackArtifact("com.github.a914-gowtham:compose-ratingbar", "0.7.0", ZonedDateTime.now()),
        )
    )
    PoodleTheme {
        SearchDependencies(
            SearchUiState(mavenCentralArtifacts, jitPackArtifacts),
            SearchQuery.EMPTY.copy(text = "compose"),
            {},
            FiltersState.Default
        )
    }
}

@Composable
@MultiThemePreview
fun SearchDependenciesErrorPreview() {
    PoodleTheme {
        SearchDependencies(
            SearchUiState(Result.Error(), Result.Error()),
            SearchQuery.EMPTY.copy(text = "compose"),
            {},
            FiltersState.Default
        )
    }
}

@Composable
@MultiThemePreview
fun SearchDependenciesLoadingPreview() {
    PoodleTheme {
        SearchDependencies(
            SearchUiState(Result.Loading, Result.Loading),
            SearchQuery.EMPTY.copy(text = "compose"),
            {},
            FiltersState.Default
        )
    }
}

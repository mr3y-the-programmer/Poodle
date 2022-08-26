package com.mr3y.poodle

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Chip
import androidx.compose.material.ChipDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FilterChip
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Scaffold
import androidx.compose.material.Switch
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mr3y.poodle.domain.SearchUiState
import com.mr3y.poodle.network.datasources.FilteringPackaging
import com.mr3y.poodle.network.exceptions.PoodleException
import com.mr3y.poodle.network.models.Result
import com.mr3y.poodle.presentation.SearchScreenViewModel
import com.mr3y.poodle.repository.Artifact
import com.mr3y.poodle.repository.SearchQuery
import com.mr3y.poodle.ui.theme.PoodleTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class, ExperimentalLifecycleComposeApi::class)
@Composable
fun SearchDependenciesScreen(viewModel: SearchScreenViewModel = viewModel()) {
    val homeState by viewModel.homeState.collectAsStateWithLifecycle(initialValue = SearchUiState.Initial)
    val bottomSheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    val query by remember { viewModel.searchQuery }
    val filters by remember {
        derivedStateOf {
            PoodleFiltersState(
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
    SearchDependencies(homeState, bottomSheetState, query, { viewModel.updateSearchQuery(searchText = it) }, filters)
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
internal fun SearchDependencies(
    state: SearchUiState,
    bottomSheetState: ModalBottomSheetState,
    searchQuery: SearchQuery,
    onSearchQueryTextChanged: (String) -> Unit,
    filtersState: PoodleFiltersState
) {
    val scope = rememberCoroutineScope()
    ModalBottomSheetLayout(
        sheetState = bottomSheetState,
        modifier = Modifier.fillMaxSize(),
        sheetContent = {
            PoodleBottomSheet(filtersState)
        }
    ) {
        val scaffoldState = rememberScaffoldState()
        Scaffold(
            scaffoldState = scaffoldState,
            topBar = {
                PoodleTopAppBar(
                    searchQuery.text,
                    onSearchQueryTextChanged,
                    isFilteringEnabled = state != SearchUiState.Initial,
                    onFilterItemsClicked = { scope.launch { bottomSheetState.show() } },
                )
            }
        ) { contentPadding ->
            val contentModifier = Modifier
                .padding(contentPadding)
                .fillMaxSize()
            if (state == SearchUiState.Initial) {
                Initial(modifier = contentModifier)
            } else {
                val selectedTabIndex = rememberSaveable(Unit) { mutableStateOf(0) }
                val tabRowHeight = 56.dp
                val exactlyOneTabExists = (state.mavenCentralArtifacts == null) xor (state.jitPackArtifacts == null)
                TabRow(
                    selectedTabIndex = if (exactlyOneTabExists) 0 else selectedTabIndex.value,
                    modifier = Modifier
                        .padding(top = contentPadding.calculateTopPadding())
                        .height(tabRowHeight),
                    backgroundColor = MaterialTheme.colors.surface,
                    contentColor = MaterialTheme.colors.primary
                ) {
                    if (exactlyOneTabExists) {
                        Tab(selected = true, onClick = { }) {
                            val label = if (state.mavenCentralArtifacts != null) "MavenCentral" else "JitPack"
                            Text(text = label)
                        }
                    } else {
                        for ((index, tabLabel) in TabLabel.values().withIndex()) {
                            Tab(selected = index == selectedTabIndex.value, onClick = { selectedTabIndex.value = index }) {
                                Text(text = tabLabel.name)
                            }
                        }
                    }
                }
                val contentState = when {
                    state.mavenCentralArtifacts != null && state.jitPackArtifacts != null -> {
                        if (selectedTabIndex.value == 0) state.mavenCentralArtifacts else state.jitPackArtifacts
                    }
                    state.mavenCentralArtifacts == null && state.jitPackArtifacts != null -> state.jitPackArtifacts
                    state.mavenCentralArtifacts != null && state.jitPackArtifacts == null -> state.mavenCentralArtifacts
                    else -> throw IllegalStateException("Tabs shouldn't be visible if UiState == Initial")
                }
                TabContent(
                    contentState,
                    searchQuery.text,
                    modifier = Modifier
                        .padding(contentPadding)
                        .padding(top = tabRowHeight)
                        .fillMaxSize()
                )
            }
        }
    }
}

@Composable
fun TabContent(
    content: Result<List<Artifact>>,
    searchQueryText: String,
    modifier: Modifier = Modifier
) {
    when (content) {
        is Result.Loading -> {
            Box(
                modifier = modifier,
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
                Empty(
                    searchQueryText = searchQueryText,
                    modifier = modifier
                )
            } else {
                DisplaySearchResults(artifacts = artifacts, modifier = modifier)
            }
        }
        is Result.Error -> {
            Error(exception = content.exception, modifier)
        }
    }
}

enum class TabLabel {
    MavenCentral,

    JitPack
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
private fun Empty(searchQueryText: String, modifier: Modifier = Modifier) {
    ArtworkWithText(
        drawableResId = if (isSystemInDarkTheme()) R.drawable.no_results_dark else R.drawable.no_results_light,
        text = "Can't find anything that matches \"${searchQueryText}\". refine your search, and try again",
        modifier = modifier
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun DisplaySearchResults(artifacts: List<Artifact>, modifier: Modifier = Modifier) {
    val page = remember(artifacts) { mutableStateOf(1..artifacts.size.coerceAtMost(20)) }
    LazyColumn(
        modifier = modifier
    ) {
        stickyHeader {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text(text = "Found ${artifacts.size} artifacts that matches your search")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Displaying artifacts: ${page.value.first} - ${page.value.last}")
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        IconButton(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .semantics { },
                            onClick = {
                                page.value = if (artifacts.size == page.value.last) {
                                    val subtracted = if (artifacts.size % 20 == 0) 20 else (artifacts.size % 20)
                                    (page.value.first - 20)..(page.value.last - subtracted)
                                } else
                                    (page.value.first - 20)..(page.value.last - 20)
                            },
                            enabled = page.value.first > 1
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
                            onClick = {
                                page.value = if ((artifacts.size - page.value.last) < 20)
                                    (page.value.last + 1)..(artifacts.size)
                                else
                                    (page.value.first + 20)..(page.value.last + 20)
                            },
                            enabled = page.value.last < artifacts.size
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
        }
        items(artifacts.slice((page.value.first - 1) until page.value.last)) { artifact ->
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
                    val chipModifier = Modifier.wrapContentHeight()
                    if (artifact is Artifact.MavenCentralArtifact) {
                        TextChip(text = artifact.packaging, chipModifier.widthIn(min = 48.dp, max = 120.dp))
                    }
                    TextChip(
                        text = "${artifact.lastUpdated?.toLocalDate() ?: "N/A"}",
                        modifier = if (artifact.lastUpdated != null) chipModifier.width(96.dp) else chipModifier.width(56.dp)
                    )
                }
                Divider()
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun TextChip(text: String, modifier: Modifier = Modifier) {
    Chip(
        onClick = { },
        colors = ChipDefaults.chipColors(
            backgroundColor = MaterialTheme.colors.primary.copy(alpha = 0.12f)
                .compositeOver(MaterialTheme.colors.surface),
            contentColor = MaterialTheme.colors.primary
        ),
        modifier = modifier
    ) {
        Text(
            text = text,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun Error(exception: PoodleException?, modifier: Modifier = Modifier) {
    val message = buildString {
        append("Unfortunately, an error occurred while trying to search for artifacts.")
        append("\nCouldn't search for artifacts due to: ")
        exception?.let { append("\n- ${it.message}") }
    }
    ArtworkWithText(
        drawableResId = if (isSystemInDarkTheme()) R.drawable.error_dark else R.drawable.error_light,
        text = message,
        modifier
    )
}

@Composable
private fun ArtworkWithText(
    drawableResId: Int,
    text: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(140.dp))
        Image(
            painter = painterResource(id = drawableResId),
            contentDescription = null,
            modifier = Modifier.size(240.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = text)
    }
}

@Composable
fun PoodleTopAppBar(
    initialSearchQuery: String,
    onSearchQueryValueChanged: (String) -> Unit,
    isFilteringEnabled: Boolean,
    onFilterItemsClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .padding(vertical = 8.dp, horizontal = 16.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TextField(
            value = initialSearchQuery,
            onValueChange = onSearchQueryValueChanged,
            modifier = Modifier.weight(6f),
            placeholder = {
                Text(text = "Search by name, groupId, or tag.")
            },
            leadingIcon = {
                Icon(
                    painter = rememberVectorPainter(image = Icons.Filled.Search),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            },
            trailingIcon =
            if (initialSearchQuery.isEmpty())
                null
            else {
                {
                    Icon(
                        painter = rememberVectorPainter(image = Icons.Filled.Close),
                        contentDescription = null,
                        modifier = Modifier
                            .size(24.dp)
                            .clickable { onSearchQueryValueChanged("") }
                    )
                }
            },
            colors = TextFieldDefaults.textFieldColors(
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                errorIndicatorColor = Color.Transparent,
            ),
            shape = RoundedCornerShape(40.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(
                onSearch = {}
            )
        )
        IconButton(
            modifier = Modifier
                .weight(1f)
                .semantics { },
            onClick = onFilterItemsClicked,
            enabled = isFilteringEnabled
        ) {
            Icon(
                painter = rememberVectorPainter(image = Icons.Filled.FilterAlt),
                contentDescription = null,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

internal data class PoodleFiltersState(
    val isMavenCentralEnabled: Boolean,
    val onMavenCentralSwitchToggled: (Boolean) -> Unit,
    val isJitpackEnabled: Boolean,
    val onJitpackSwitchToggled: (Boolean) -> Unit,
    val groupIdValue: String,
    val onGroupIdValueChanged: (String) -> Unit,
    val packagingValue: String,
    val onPackagingValueChanged: (String) -> Unit,
    val tagsValue: Set<String>,
    val onTagsValueChanged: (Set<String>) -> Unit,
    val limitValue: Int?,
    val onLimitValueChanged: (Int?) -> Unit,
    val classSimpleNameValue: String,
    val onClassSimpleNameValueChanged: (String) -> Unit,
    val classFQNValue: String,
    val onClassFQNValueChanged: (String) -> Unit,
) {
    companion object {
        val Default = PoodleFiltersState(
            true,
            {},
            true,
            {},
            "",
            {},
            "",
            {},
            emptySet(),
            {},
            null,
            {},
            "",
            {},
            "",
            {}
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun PoodleBottomSheet(
    state: PoodleFiltersState,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        FilterHeader()
        val childModifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
        Row(
            modifier = childModifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            repeat(2) { index ->
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterSwitchField(
                        label = if (index == 0) "MavenCentral" else "Jitpack",
                        enabled = if (index == 0) state.isMavenCentralEnabled else state.isJitpackEnabled,
                        onToggled = if (index == 0) state.onMavenCentralSwitchToggled else state.onJitpackSwitchToggled
                    )
                }
            }
        }
        Row(
            modifier = childModifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            FilterTextField(
                initialValue = state.groupIdValue,
                onValueChange = state.onGroupIdValueChanged,
                label = "GroupId:",
                modifier = Modifier.weight(1f)
            )
            FilterTextField(
                initialValue = state.limitValue?.toString() ?: "",
                onValueChange = { state.onLimitValueChanged(it.toIntOrNull()) },
                label = "Limit:",
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        }
        Row(
            modifier = childModifier,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = "Packaging *: ")
            val packages = remember { FilteringPackaging }
            repeat(3) { index ->
                FilterChip(
                    selected = state.packagingValue == packages.elementAt(index),
                    onClick = {
                        val newValue = if (state.packagingValue == packages.elementAt(index)) {
                            ""
                        } else {
                            packages.elementAt(index)
                        }
                        state.onPackagingValueChanged(newValue)
                    },
                    border = ChipDefaults.outlinedBorder,
                    colors = ChipDefaults.outlinedFilterChipColors(),
                    selectedIcon = {
                        Icon(
                            imageVector = Icons.Filled.Done,
                            contentDescription = null,
                            modifier = Modifier.requiredSize(ChipDefaults.SelectedIconSize)
                        )
                    }
                ) {
                    Text(text = packages.elementAt(index))
                }
            }
        }
        Column(
            modifier = childModifier,
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(text = "Advanced *")
            FilterTextField(
                initialValue = state.classSimpleNameValue,
                onValueChange = state.onClassSimpleNameValueChanged,
                label = "Contains class simple name:",
                modifier = Modifier.fillMaxWidth()
            )
            FilterTextField(
                initialValue = state.classFQNValue,
                onValueChange = state.onClassFQNValueChanged,
                label = "Contains class FQN:",
                modifier = Modifier.fillMaxWidth()
            )
        }
        Text(
            text = "*: only mavenCentral supported",
            modifier = childModifier
        )
    }
}

@Composable
fun FilterHeader(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = "Filters")
        Icon(
            painter = rememberVectorPainter(image = Icons.Filled.KeyboardArrowUp),
            contentDescription = null,
            modifier = Modifier.size(32.dp)
        )
    }
}

@Composable
fun FilterSwitchField(
    label: String,
    enabled: Boolean,
    onToggled: (Boolean) -> Unit,
) {
    Text(
        text = label,
    )
    Switch(
        checked = enabled,
        onCheckedChange = onToggled,
    )
}

@Composable
fun FilterTextField(
    initialValue: String,
    onValueChange: (newValue: String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    TextField(
        value = initialValue,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        singleLine = true,
        label = {
            Text(text = label)
        },
        trailingIcon =
        if (initialValue.isEmpty())
            null
        else {
            {
                Icon(
                    painter = rememberVectorPainter(image = Icons.Filled.Close),
                    contentDescription = null,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { onValueChange("") }
                )
            }
        },
        keyboardOptions = keyboardOptions,
        shape = RoundedCornerShape(4.dp),
        colors = TextFieldDefaults.textFieldColors(
            errorIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        )
    )
}

@Preview
@Composable
@OptIn(ExperimentalMaterialApi::class)
fun SearchDependenciesPreview() {
    PoodleTheme(false) {
        val bottomSheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
        SearchDependencies(
            SearchUiState.Initial,
            bottomSheetState,
            SearchQuery.EMPTY,
            {},
            PoodleFiltersState.Default
        )
    }
}

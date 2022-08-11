package com.mr3y.poodle

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
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mr3y.poodle.network.datasources.FilteringPackaging
import com.mr3y.poodle.network.exceptions.PoodleException
import com.mr3y.poodle.presentation.SearchScreenState
import com.mr3y.poodle.presentation.SearchScreenViewModel
import com.mr3y.poodle.repository.Artifact
import com.mr3y.poodle.repository.SearchQuery

@OptIn(ExperimentalMaterialApi::class, ExperimentalLifecycleComposeApi::class)
@Composable
fun SearchDependenciesScreen(viewModel: SearchScreenViewModel = viewModel()) {
    val homeState by viewModel.homeState.collectAsStateWithLifecycle(initialValue = SearchScreenState.Initial)
    val bottomSheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.HalfExpanded)
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
    state: SearchScreenState,
    bottomSheetState: ModalBottomSheetState,
    searchQuery: SearchQuery,
    onSearchQueryTextChanged: (String) -> Unit,
    filtersState: PoodleFiltersState
) {
    ModalBottomSheetLayout(
        sheetState = bottomSheetState,
        modifier = Modifier.fillMaxSize(),
        sheetContent = {
            PoodleBottomSheet(filtersState)
        },
        sheetShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
    ) {
        val scaffoldState = rememberScaffoldState()
        Scaffold(
            scaffoldState = scaffoldState,
            topBar = {
                PoodleTopAppBar(
                    searchQuery.text,
                    onSearchQueryTextChanged,
                )
            }
        ) { contentPadding ->
            val contentModifier = Modifier
                .padding(contentPadding)
                .fillMaxSize()
            when {
                state.isIdle -> {
                    Initial(modifier = contentModifier)
                }
                state.isLoading -> {
                    Box(
                        modifier = contentModifier,
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }
                state.shouldShowArtifacts -> {
                    if (state.artifacts.isEmpty()) {
                        Empty(
                            searchQueryText = searchQuery.text,
                            modifier = contentModifier
                        )
                    } else {
                        DisplaySearchResults(artifacts = state.artifacts, modifier = contentModifier)
                    }
                }
                state.shouldShowExceptions -> {
                    Error(exceptions = state.exceptions, contentModifier)
                }
                state.shouldShowArtifactsAndExceptions -> {
                    if (state.artifacts.isEmpty()) {
                        Empty(searchQueryText = searchQuery.text, modifier = contentModifier)
                    } else {
                        DisplaySearchResults(artifacts = state.artifacts, contentModifier)
                    }
                    LaunchedEffect(key1 = Unit) {
                        state.exceptions.forEach { exception ->
                            scaffoldState.snackbarHostState.showSnackbar("Error: ${exception.message}")
                        }
                    }
                }
            }
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
private fun Empty(searchQueryText: String, modifier: Modifier = Modifier) {
    ArtworkWithText(
        drawableResId = if (isSystemInDarkTheme()) R.drawable.no_results_dark else R.drawable.no_results_light,
        text = "Can't find anything that matches \"${searchQueryText}\". refine your search, and try again",
        modifier = modifier
    )
}

@Composable
private fun DisplaySearchResults(artifacts: List<Artifact>, modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier
    ) {
        items(artifacts) { artifact ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(text = artifact.fullId)
                if (artifact is Artifact.MavenCentralArtifact) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TextChip(text = artifact.latestVersion, Modifier.wrapContentWidth())
                        TextChip(text = artifact.packaging, Modifier.wrapContentWidth())
                        TextChip(text = "${artifact.lastUpdated.toLocalDate()}", Modifier.wrapContentWidth())
                    }
                }
                val source = if (artifact is Artifact.MavenCentralArtifact) "MavenCentral" else "JitPack"
                TextChip(text = "hosted on: $source", Modifier.align(Alignment.End))
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
        Text(text = text)
    }
}

@Composable
private fun Error(exceptions: List<PoodleException>, modifier: Modifier = Modifier) {
    val message = buildString {
        append("Unfortunately, an error occurred while trying to search for artifacts.")
        append("\nCouldn't search for artifacts due to: ")
        exceptions.forEach { exception ->
            append("\n- ${exception.message}")
        }
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
                .size(48.dp)
                .semantics {
                    /*contentDescription = TODO*/
                },
            onClick = { /*TODO*/ }
        ) {
            Icon(
                painter = rememberVectorPainter(image = Icons.Filled.Sort),
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
    val bottomSheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.HalfExpanded)
    SearchDependencies(
        SearchScreenState.Initial,
        bottomSheetState,
        SearchQuery.EMPTY,
        {},
        PoodleFiltersState.Default
    )
}

package com.mr3y.poodle

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Scaffold
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SearchDependencies() {
    val bottomSheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.HalfExpanded)
    var filters by remember { mutableStateOf(PoodleFiltersState.Default) }
    var query by remember { mutableStateOf("") }
    ModalBottomSheetLayout(
        sheetState = bottomSheetState,
        modifier = Modifier.fillMaxSize(),
        sheetContent = {
            PoodleBottomSheet(filters)
        },
        sheetShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
    ) {
        Scaffold(
            topBar = {
                PoodleTopAppBar(query, { query = it })
            }
        ) { contentPadding ->
            LazyColumn(modifier = Modifier.padding(contentPadding)) {
                // TODO: handle different states(Initial, Content, EmptyResults, AutoComplete) by showing different artworks
            }
        }
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
                errorIndicatorColor = Color.Transparent
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

private data class PoodleFiltersState(
    val isMavenCentralEnabled: Boolean,
    val onMavenCentralSwitchToggled: (Boolean) -> Unit,
    val isJitpackEnabled: Boolean,
    val onJitpackSwitchToggled: (Boolean) -> Unit,
    val groupIdInitialValue: String,
    val onGroupIdValueChanged: (String) -> Unit,
    val packagingInitialValue: String,
    val onPackagingValueChanged: (String) -> Unit,
    val tagsInitialValue: String,
    val onTagsValueChanged: (String) -> Unit,
    val limitInitialValue: Int?,
    val onLimitValueChanged: (Int?) -> Unit,
    val classSimpleNameInitialValue: String,
    val onClassSimpleNameValueChanged: (String) -> Unit,
    val classFQNInitialValue: String,
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
            "",
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
                initialValue = state.groupIdInitialValue,
                onValueChange = state.onGroupIdValueChanged,
                label = "GroupId:",
                modifier = Modifier.weight(1f)
            )
            FilterTextField(
                initialValue = state.packagingInitialValue,
                onValueChange = state.onPackagingValueChanged,
                label = "Packaging: *",
                modifier = Modifier.weight(1f)
            )
        }
        Row(
            modifier = childModifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            FilterTextField(
                initialValue = state.tagsInitialValue,
                onValueChange = state.onTagsValueChanged,
                label = "Tags: *",
                modifier = Modifier.weight(1f)
            )
            FilterTextField(
                initialValue = state.limitInitialValue?.toString() ?: "",
                onValueChange = { state.onLimitValueChanged(it.toInt()) },
                label = "Limit:",
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        }
        Column(
            modifier = childModifier,
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(text = "Advanced *")
            FilterTextField(
                initialValue = state.classSimpleNameInitialValue,
                onValueChange = state.onClassSimpleNameValueChanged,
                label = "Contains class simple name:",
                modifier = Modifier.fillMaxWidth()
            )
            FilterTextField(
                initialValue = state.classFQNInitialValue,
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
fun SearchDependenciesPreview() {
    SearchDependencies()
}

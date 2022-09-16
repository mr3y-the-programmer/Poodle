package com.mr3y.poodle.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ChipDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FilterChip
import androidx.compose.material.Icon
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.mr3y.poodle.network.datasources.FilteringPackaging
import com.mr3y.poodle.ui.preview_utils.MultiThemePreview
import com.mr3y.poodle.ui.theme.PoodleTheme

internal data class FiltersState(
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
        val Default = FiltersState(
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
internal fun FiltersBottomSheet(
    state: FiltersState,
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
internal fun FilterHeader(modifier: Modifier = Modifier) {
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
internal fun FilterSwitchField(
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
internal fun FilterTextField(
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

@MultiThemePreview
@Composable
fun FilterHeaderPreview() {
    PoodleTheme {
        FilterHeader(
            Modifier
                .fillMaxWidth()
                .height(80.dp)
        )
    }
}

@MultiThemePreview
@Composable
fun FilterSwitchFieldPreview() {
    PoodleTheme {
        FilterSwitchField(label = "Switch", enabled = true, onToggled = {})
    }
}

@MultiThemePreview
@Composable
fun FilterTextFieldPreview() {
    PoodleTheme {
        FilterTextField(
            initialValue = "Some user input", onValueChange = {}, label = "TextField",
            Modifier
                .fillMaxWidth()
                .height(64.dp)
        )
    }
}

@MultiThemePreview
@Composable
fun FiltersBottomSheetPreview() {
    PoodleTheme {
        val previewState = FiltersState.Default.copy(groupIdValue = "com.google.*", packagingValue = "aar", limitValue = 15)
        FiltersBottomSheet(state = previewState, modifier = Modifier.fillMaxSize())
    }
}

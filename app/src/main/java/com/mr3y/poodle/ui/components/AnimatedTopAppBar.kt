package com.mr3y.poodle.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActionScope
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.mr3y.poodle.ui.preview_utils.MultiThemePreview
import com.mr3y.poodle.ui.theme.PoodleTheme

@Composable
internal fun AnimatedTopAppBar(
    initialSearchQuery: String,
    onSearchQueryValueChanged: (String) -> Unit,
    isFilteringEnabled: Boolean,
    onFilterItemsClicked: () -> Unit,
    rootInteractionSource: MutableInteractionSource,
    modifier: Modifier = Modifier
) {
    val textFieldInteractionSource = remember { MutableInteractionSource() }
    val isFocused by textFieldInteractionSource.collectIsFocusedAsState()
    val focusManager = LocalFocusManager.current
    var searchBarBounds by remember { mutableStateOf<Rect?>(null) }
    LaunchedEffect(Unit) {
        rootInteractionSource.interactions.collect { interaction ->
            if (interaction is PressInteraction.Press) {
                if (searchBarBounds != null && searchBarBounds?.contains(interaction.pressPosition) == false) {
                    focusManager.clearFocus()
                }
            }
        }
    }
    Row(
        modifier = modifier
            .padding(vertical = 8.dp, horizontal = 16.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val fraction by animateFloatAsState(
            targetValue = if (isFocused) 1f else 0.85f,
            animationSpec = tween(600)
        )
        SearchBar(
            initialSearchQuery = initialSearchQuery,
            interactionSource = textFieldInteractionSource,
            onSearchQueryValueChanged = onSearchQueryValueChanged,
            onSearch = { focusManager.clearFocus() },
            modifier = Modifier
                .fillMaxWidth(fraction)
                .onPlaced {
                    searchBarBounds = it.boundsInRoot()
                }
        )
        if (!isFocused) {
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                modifier = Modifier
                    .semantics {
                        contentDescription = "Filter results"
                        stateDescription =
                            if (isFilteringEnabled) "Click to Display some filters to filter out irrelevant items" else "Filters are disabled, because your search query is empty"
                    },
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
}

@Composable
private fun SearchBar(
    initialSearchQuery: String,
    interactionSource: MutableInteractionSource,
    onSearchQueryValueChanged: (String) -> Unit,
    onSearch: KeyboardActionScope.() -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        value = initialSearchQuery,
        interactionSource = interactionSource,
        onValueChange = onSearchQueryValueChanged,
        modifier = modifier,
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
        keyboardActions = KeyboardActions(onSearch = onSearch)
    )
}

@MultiThemePreview
@Composable
fun TopAppBarPreview() {
    PoodleTheme {
        AnimatedTopAppBar(
            initialSearchQuery = "compose",
            onSearchQueryValueChanged = {},
            isFilteringEnabled = true,
            onFilterItemsClicked = { },
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp),
            rootInteractionSource = remember { MutableInteractionSource() }
        )
    }
}

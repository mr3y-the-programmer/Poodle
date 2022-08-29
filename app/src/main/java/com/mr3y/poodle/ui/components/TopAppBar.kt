package com.mr3y.poodle.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mr3y.poodle.ui.theme.PoodleTheme

@Composable
internal fun TopAppBar(
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

@Preview(showBackground = true)
@Composable
fun TopAppBarPreview() {
    PoodleTheme(false) {
        TopAppBar(
            initialSearchQuery = "compose",
            onSearchQueryValueChanged = {},
            isFilteringEnabled = true,
            onFilterItemsClicked = { },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

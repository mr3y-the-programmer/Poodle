package com.mr3y.poodle.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp

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

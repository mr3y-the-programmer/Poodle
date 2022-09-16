package com.mr3y.poodle.ui.preview_utils

import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Preview

@Preview(name = "light theme", uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
@Preview(name = "dark theme", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
annotation class MultiThemePreview

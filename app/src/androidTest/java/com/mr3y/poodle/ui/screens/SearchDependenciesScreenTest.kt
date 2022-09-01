package com.mr3y.poodle.ui.screens

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import com.mr3y.poodle.domain.SearchUiState
import com.mr3y.poodle.repository.SearchQuery
import com.mr3y.poodle.ui.components.FiltersState
import com.mr3y.poodle.ui.theme.PoodleTheme
import org.junit.Rule
import org.junit.Test

class SearchDependenciesScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun searchDependenciesScreen_initialState_BottomSheetIsNotDisplayed() {
        composeTestRule.setContent {
            PoodleTheme {
                SearchDependencies(
                    state = SearchUiState.Initial,
                    searchQuery = SearchQuery.EMPTY,
                    onSearchQueryTextChanged = {},
                    filtersState = FiltersState.Default
                )
            }
        }

        composeTestRule.onNodeWithText("Search for any artifacts On MavenCentral, Or JitPack").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Filter results").apply {
            assertIsDisplayed()
            assertIsNotEnabled()
        }
        composeTestRule.onNodeWithText("Search by name, groupId, or tag.").assertIsDisplayed()
        composeTestRule.onNodeWithText("Filters").assertIsNotDisplayed()
    }
}

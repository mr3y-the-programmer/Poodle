package com.mr3y.poodle.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.mr3y.poodle.domain.SearchUiState
import com.mr3y.poodle.network.models.Result
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
        composeTestRule.onNodeWithText("Filters", useUnmergedTree = true).assertIsNotDisplayed()
    }

    @Test
    fun searchDependenciesScreen_loadingState_TabIsVisibleAndBottomSheetCanBeDisplayed() {
        val uiState by mutableStateOf(SearchUiState(Result.Loading, null))
        composeTestRule.setContent {
            PoodleTheme {
                SearchDependencies(
                    state = remember { uiState },
                    searchQuery = SearchQuery.EMPTY.copy(text = "compose"),
                    onSearchQueryTextChanged = {},
                    filtersState = FiltersState.Default
                )
            }
        }

        composeTestRule.onNode(hasText("MavenCentral") and hasRole(Role.Tab)).assertIsDisplayed()
        composeTestRule.onNodeWithTag("LoadingIndicator", useUnmergedTree = true).assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Filter results").apply {
            assertIsDisplayed()
            assertIsEnabled()
        }
        composeTestRule.onNodeWithText("Filters", useUnmergedTree = true).assertIsNotDisplayed()
        composeTestRule.onNodeWithContentDescription("Filter results").performClick()
        composeTestRule.onNodeWithText("Filters").assertIsDisplayed()
    }

    private fun hasRole(value: Role): SemanticsMatcher = SemanticsMatcher.expectValue(SemanticsProperties.Role, value)
}

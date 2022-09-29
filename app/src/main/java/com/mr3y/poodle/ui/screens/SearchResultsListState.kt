package com.mr3y.poodle.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.mr3y.poodle.repository.Artifact

const val DefaultPageSize = 20

@Composable
fun rememberSearchResultsListState(
    artifacts: List<Artifact>,
    totalNumOfAllMatchedArtifacts: Int = artifacts.size,
    numOfArtifactsPerPage: Int = DefaultPageSize
): SearchResultsListState {
    return remember(artifacts) { SearchResultsListState(artifacts, totalNumOfAllMatchedArtifacts, numOfArtifactsPerPage) }
}

class SearchResultsListState(
    private val artifacts: List<Artifact>,
    val totalNumOfAllMatchedArtifacts: Int,
    val numOfArtifactsPerPage: Int
) {
    var currentPage by mutableStateOf(1..totalNumOfAllMatchedArtifacts.coerceAtMost(numOfArtifactsPerPage))
        private set

    val currentPageArtifacts by derivedStateOf { artifacts.slice((currentPage.first - 1) until currentPage.last) }

    val isFirstPage by derivedStateOf { currentPage.first == 1 }

    val isLastPage by derivedStateOf { currentPage.last == totalNumOfAllMatchedArtifacts }

    fun goToNextPage() {
        currentPage = if ((totalNumOfAllMatchedArtifacts - currentPage.last) < numOfArtifactsPerPage)
            (currentPage.last + 1)..(totalNumOfAllMatchedArtifacts)
        else
            (currentPage.first + numOfArtifactsPerPage)..(currentPage.last + numOfArtifactsPerPage)
    }

    fun backToThePreviousPage() {
        currentPage = if (totalNumOfAllMatchedArtifacts == currentPage.last) {
            val subtracted = if (totalNumOfAllMatchedArtifacts % numOfArtifactsPerPage == 0) numOfArtifactsPerPage else (totalNumOfAllMatchedArtifacts % numOfArtifactsPerPage)
            (currentPage.first - numOfArtifactsPerPage)..(currentPage.last - subtracted)
        } else
            (currentPage.first - numOfArtifactsPerPage)..(currentPage.last - numOfArtifactsPerPage)
    }
}

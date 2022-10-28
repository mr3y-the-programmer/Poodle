package com.mr3y.poodle.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.mr3y.poodle.repository.Artifact

const val DefaultPageSize = 20

@Composable
fun rememberSearchResultsListState(
    artifacts: List<Artifact>,
    totalNumOfAllMatchedArtifacts: Int = artifacts.size,
    numOfArtifactsPerPage: Int = DefaultPageSize
): SearchResultsListState {
    return rememberSaveable(artifacts, saver = SearchResultsListState.Saver()) {
        SearchResultsListState(totalNumOfAllMatchedArtifacts, numOfArtifactsPerPage)
    }
}

class SearchResultsListState(
    val totalNumOfAllMatchedArtifacts: Int,
    val numOfArtifactsPerPage: Int
) {
    var currentPage by mutableStateOf(1..totalNumOfAllMatchedArtifacts.coerceAtMost(numOfArtifactsPerPage))
        private set

    val isFirstPage by derivedStateOf { currentPage.first == 1 }

    val isLastPage by derivedStateOf { currentPage.last == totalNumOfAllMatchedArtifacts }

    fun goToNextPage() {
        if (isLastPage) return

        currentPage = if ((totalNumOfAllMatchedArtifacts - currentPage.last) < numOfArtifactsPerPage)
            (currentPage.last + 1)..(totalNumOfAllMatchedArtifacts)
        else
            (currentPage.first + numOfArtifactsPerPage)..(currentPage.last + numOfArtifactsPerPage)
    }

    fun backToThePreviousPage() {
        if (isFirstPage) return

        currentPage = if (totalNumOfAllMatchedArtifacts == currentPage.last) {
            val subtracted = if (totalNumOfAllMatchedArtifacts % numOfArtifactsPerPage == 0) numOfArtifactsPerPage else (totalNumOfAllMatchedArtifacts % numOfArtifactsPerPage)
            (currentPage.first - numOfArtifactsPerPage)..(currentPage.last - subtracted)
        } else
            (currentPage.first - numOfArtifactsPerPage)..(currentPage.last - numOfArtifactsPerPage)
    }

    fun getCurrentPageArtifactsOf(allArtifacts: List<Artifact>) = allArtifacts.slice((currentPage.first - 1) until currentPage.last)

    companion object {

        fun Saver() = Saver<SearchResultsListState, IntArray>(
            save = { state ->
                intArrayOf(state.totalNumOfAllMatchedArtifacts, state.numOfArtifactsPerPage, state.currentPage.first, state.currentPage.last)
            },
            restore = {
                val restored = SearchResultsListState(it[0], it[1])
                restored.currentPage = it[2]..it[3]
                restored
            }
        )
    }
}

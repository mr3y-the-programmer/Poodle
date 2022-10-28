package com.mr3y.poodle.ui.screens

import com.google.common.truth.Truth.assertThat
import com.google.testing.junit.testparameterinjector.TestParameterInjector
import com.google.testing.junit.testparameterinjector.TestParameters
import com.mr3y.poodle.repository.fakeArtifacts
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(TestParameterInjector::class)
class SearchResultsListStateTest {

    private lateinit var sut: SearchResultsListState

    @Test
    @TestParameters("{additionalValue: 0}")
    @TestParameters("{additionalValue: 10}")
    fun `given a number of artifacts per page that is higher than or equal to all matched artifacts number, then verify we have exactly one page`(
        additionalValue: Int
    ) {
        sut = SearchResultsListState(fakeArtifacts.size, fakeArtifacts.size + additionalValue)

        assertThat(sut.currentPage).isEqualTo(1..fakeArtifacts.size)
        assertThat(sut.getCurrentPageArtifactsOf(fakeArtifacts)).isEqualTo(fakeArtifacts)
        assertThat(sut.isFirstPage).isTrue()
        assertThat(sut.isLastPage).isTrue()

        // should be no-op
        sut.goToNextPage()
        assertThat(sut.currentPage).isEqualTo(1..fakeArtifacts.size)
        assertThat(sut.getCurrentPageArtifactsOf(fakeArtifacts)).isEqualTo(fakeArtifacts)

        // should be no-op
        sut.backToThePreviousPage()
        assertThat(sut.currentPage).isEqualTo(1..fakeArtifacts.size)
        assertThat(sut.getCurrentPageArtifactsOf(fakeArtifacts)).isEqualTo(fakeArtifacts)
    }

    @Test
    fun `given a number of matched artifacts that is higher than number of artifacts per page, then verify we have more than one page`() {
        val artifacts = buildList {
            addAll(fakeArtifacts)
            addAll(fakeArtifacts)
            addAll(fakeArtifacts)
        }
        val numberOfArtifactsPerPage = artifacts.size - 11
        sut = SearchResultsListState(artifacts.size, numberOfArtifactsPerPage)

        fun assertWeAreOnFirstPage() {
            assertThat(sut.currentPage).isEqualTo(1..numberOfArtifactsPerPage)
            assertThat(sut.getCurrentPageArtifactsOf(artifacts)).isEqualTo(artifacts.subList(0, numberOfArtifactsPerPage))
            assertThat(sut.isFirstPage).isTrue()
            assertThat(sut.isLastPage).isFalse()
        }
        assertWeAreOnFirstPage()

        sut.goToNextPage()

        fun assertWeAreOnSecondPage() {
            assertThat(sut.currentPage).isEqualTo((numberOfArtifactsPerPage + 1)..(numberOfArtifactsPerPage * 2))
            assertThat(sut.getCurrentPageArtifactsOf(artifacts)).isEqualTo(artifacts.subList(numberOfArtifactsPerPage, (numberOfArtifactsPerPage * 2)))
            assertThat(sut.isFirstPage).isFalse()
            assertThat(sut.isLastPage).isFalse()
        }
        assertWeAreOnSecondPage()

        sut.goToNextPage()

        // last page
        assertThat(sut.currentPage).isEqualTo((numberOfArtifactsPerPage * 2 + 1)..(numberOfArtifactsPerPage * 2 + 1))
        assertThat(sut.getCurrentPageArtifactsOf(artifacts)).isEqualTo(listOf(artifacts[artifacts.lastIndex]))
        assertThat(sut.isFirstPage).isFalse()
        assertThat(sut.isLastPage).isTrue()

        sut.backToThePreviousPage()

        assertWeAreOnSecondPage()

        sut.backToThePreviousPage()

        assertWeAreOnFirstPage()
    }
}

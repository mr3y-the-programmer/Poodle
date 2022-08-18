package com.mr3y.poodle.domain

import com.mr3y.poodle.network.models.Result
import com.mr3y.poodle.repository.SearchResult
import com.mr3y.poodle.repository.Source
import com.mr3y.poodle.repository.fakeArtifactsPart1
import com.mr3y.poodle.repository.fakeArtifactsPart2
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow

internal val fakeSearchResults = flow {
    emit(SearchResult(Result.Loading, Source.JitPack))
    delay(2)
    emit(SearchResult(Result.Loading, Source.MavenCentral))
    delay(200)
    emit(SearchResult(Result.Success(fakeArtifactsPart1), Source.MavenCentral))
    delay(300)
    emit(SearchResult(Result.Success(fakeArtifactsPart2), Source.JitPack))
}

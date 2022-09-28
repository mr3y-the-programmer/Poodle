package com.mr3y.poodle.network.fixtures

import com.mr3y.poodle.network.JitPackQueryParameters
import com.mr3y.poodle.network.datasources.JitPack
import com.mr3y.poodle.network.models.JitPackResponse
import com.mr3y.poodle.network.models.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeJitPackImpl : JitPack {

    lateinit var response: Result<JitPackResponse>

    override fun getArtifacts(queryParameters: JitPackQueryParameters.() -> Unit): Flow<Result<JitPackResponse>> {
        return flowOf(
            Result.Loading,
            response
        )
    }
}

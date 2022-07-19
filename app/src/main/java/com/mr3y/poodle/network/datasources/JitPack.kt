package com.mr3y.poodle.network.datasources

import com.mr3y.poodle.network.JitPackQueryParameters
import com.mr3y.poodle.network.models.JitPackResponse
import com.mr3y.poodle.network.models.Result
import kotlinx.coroutines.flow.Flow

interface JitPack {

    fun getArtifacts(queryParameters: JitPackQueryParameters.() -> Unit): Flow<Result<JitPackResponse>>
}

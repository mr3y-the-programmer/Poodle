package com.mr3y.poodle.network.datasources

import com.mr3y.poodle.network.JitPackQueryParameters
import com.mr3y.poodle.network.models.JitPackResponse
import com.mr3y.poodle.network.models.Result

interface JitPack {

    suspend fun getArtifacts(queryParameters: JitPackQueryParameters.() -> Unit): Result<JitPackResponse>
}

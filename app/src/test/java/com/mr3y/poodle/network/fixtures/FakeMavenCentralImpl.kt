package com.mr3y.poodle.network.fixtures

import com.mr3y.poodle.network.MavenCentralQueryParameters
import com.mr3y.poodle.network.datasources.MavenCentral
import com.mr3y.poodle.network.models.MavenCentralResponse
import com.mr3y.poodle.network.models.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeMavenCentralImpl : MavenCentral {

    lateinit var response: Result<MavenCentralResponse>

    override fun getArtifacts(queryParameters: MavenCentralQueryParameters.() -> Unit): Flow<Result<MavenCentralResponse>> {
        return flowOf(
            Result.Loading,
            response
        )
    }
}

package com.mr3y.poodle.network.datasources

import com.mr3y.poodle.network.MavenCentralQueryParameters
import com.mr3y.poodle.network.models.MavenCentralResponse
import com.mr3y.poodle.network.models.Result
import kotlinx.coroutines.flow.Flow

interface MavenCentral {

    fun getArtifacts(queryParameters: MavenCentralQueryParameters.() -> Unit): Flow<Result<MavenCentralResponse>>
}

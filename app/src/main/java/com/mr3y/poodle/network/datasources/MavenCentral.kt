package com.mr3y.poodle.network.datasources

import com.mr3y.poodle.network.MavenCentralQueryParameters
import com.mr3y.poodle.network.models.MavenCentralResponse
import com.mr3y.poodle.network.models.Result

interface MavenCentral {

    suspend fun getArtifacts(queryParameters: MavenCentralQueryParameters.() -> Unit): Result<MavenCentralResponse>
}

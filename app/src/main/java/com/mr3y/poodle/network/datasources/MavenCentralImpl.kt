package com.mr3y.poodle.network.datasources

import com.mr3y.poodle.di.MavenCentralBaseUrl
import com.mr3y.poodle.network.MavenCentralQueryParameters
import com.mr3y.poodle.network.models.MavenCentralResponse
import com.mr3y.poodle.network.models.Result
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

val FilteringPackaging = setOf("pom", "jar", "aar")

class MavenCentralImpl @Inject constructor(
    private val client: HttpClient,
    @MavenCentralBaseUrl
    private val baseUrl: String
) : MavenCentral {

    override fun getArtifacts(queryParameters: MavenCentralQueryParameters.() -> Unit): Flow<Result<MavenCentralResponse>> {
        val requestQueryParams = MavenCentralQueryParameters.apply(queryParameters)
        return flow {
            emit(Result.Loading)
            val response: MavenCentralResponse = client.get(baseUrl) {
                val query = MavenCentralQueryParameters.getNormalizedStringQueryParameter()
                parameter("q", query)
                parameter("rows", requestQueryParams.limit.takeIf { it > 0 && it != Int.MAX_VALUE })
                parameter("start", requestQueryParams.start)
                parameter("wt", "json")
            }.body()
            Napier.d("$response")
            emit(Result.Success(response))
        }.catch { throwable ->
            Napier.e("An exception occurred while trying to get mavenCentral artifacts", throwable)
            emit(Result.Error(throwable))
        }
    }
}

internal fun MavenCentralQueryParameters.getNormalizedStringQueryParameter(): String? {
    val result = StringBuilder()
    text.takeIf { it.isNotEmpty() }?.let {
        result.append(it)
        result.append(" AND ")
    }
    groupId.takeIf { it.isNotEmpty() }?.let {
        result.append("g:$it")
        result.append(" AND ")
    }
    packaging.takeIf { it.isNotEmpty() && it in FilteringPackaging }?.let {
        result.append("p:$it")
        result.append(" AND ")
    }
    containsClassSimpleName.takeIf { it.isNotEmpty() }?.let {
        result.append("c:$it")
        result.append(" AND ")
    }
    containsClassFullyQualifiedName.takeIf { it.isNotEmpty() }?.let {
        result.append("fc:$it")
        result.append(" AND ")
    }
    tags.takeIf { it.isNotEmpty() }?.forEach { tag ->
        result.append("tags:$tag")
        result.append(" AND ")
    }
    // make sure to drop the last AND
    return if (result.isNotEmpty()) result.dropLast(5).toString() else null
}

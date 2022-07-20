package com.mr3y.poodle.network.datasources

import com.mr3y.poodle.di.JitPackBaseUrl
import com.mr3y.poodle.network.JitPackQueryParameters
import com.mr3y.poodle.network.exceptions.toPoodleException
import com.mr3y.poodle.network.models.JitPackArtifact
import com.mr3y.poodle.network.models.JitPackResponse
import com.mr3y.poodle.network.models.Result
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.path
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.JsonObject
import javax.inject.Inject

class JitPackImpl @Inject constructor(
    private val client: HttpClient,
    @JitPackBaseUrl
    private val baseUrl: String
) : JitPack {

    override fun getArtifacts(queryParameters: JitPackQueryParameters.() -> Unit): Flow<Result<JitPackResponse>> {
        val requestQueryParameters = JitPackQueryParameters.apply(queryParameters)
        return flow {
            emit(Result.Loading)
            var isGroupIdEmpty = true
            // we have to return a jsonObject & transform it manually as JitPack API is poorly designed
            val response: JsonObject = client.get(baseUrl) {
                url {
                    path("search")
                    requestQueryParameters.groupId.takeIf { it.isNotEmpty() }?.let {
                        isGroupIdEmpty = false
                        parameters.append("q", "$it:")
                    }
                    requestQueryParameters.text.takeIf { it.isNotEmpty() && isGroupIdEmpty }?.let {
                        parameters.append("q", it)
                    }
                    requestQueryParameters.limit.takeIf { it > 0 && it != Int.MAX_VALUE }?.let {
                        parameters.append("limit", it.toString())
                    }
                }
            }.body()
            val jitPackResponse = response
                .toJitPackResponse()
                // because JitPack API doesn't allow text & groupId in one query
                .filterRelevantResponses(
                    !isGroupIdEmpty && requestQueryParameters.text.isNotEmpty(),
                    requestQueryParameters.text
                )
            emit(Result.Success(jitPackResponse))
        }.catch { throwable ->
            emit(Result.Error(throwable.toPoodleException(isMavenCentralServer = false)))
        }
    }

    private fun JsonObject.toJitPackResponse(): JitPackResponse {
        return JitPackResponse(keys.map { JitPackArtifact(id = it) })
    }

    private fun JitPackResponse.filterRelevantResponses(enabled: Boolean, text: String): JitPackResponse {
        return if (enabled)
            copy(artifacts = artifacts.filter { it.id.split(":")[1].contains(text, ignoreCase = true) })
        else
            this
    }
}

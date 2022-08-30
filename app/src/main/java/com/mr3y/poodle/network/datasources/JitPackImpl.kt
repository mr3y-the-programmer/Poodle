package com.mr3y.poodle.network.datasources

import com.mr3y.poodle.di.JitPackBaseUrl
import com.mr3y.poodle.network.JitPackQueryParameters
import com.mr3y.poodle.network.exceptions.toPoodleException
import com.mr3y.poodle.network.models.JitPackArtifact
import com.mr3y.poodle.network.models.JitPackArtifactCoordinates
import com.mr3y.poodle.network.models.JitPackResponse
import com.mr3y.poodle.network.models.PartialJitPackResponse
import com.mr3y.poodle.network.models.Result
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.isSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.sync.Semaphore
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.long
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
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
            val response: JsonObject = client.get("$baseUrl/search") {
                url {
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
                .toPartialJitPackResponse()
                // because JitPack API doesn't allow text & groupId in one query
                .filterRelevantResponses(
                    !isGroupIdEmpty && requestQueryParameters.text.isNotEmpty(),
                    requestQueryParameters.text
                ).toFullResponse()

            Napier.d("$jitPackResponse")
            emit(Result.Success(jitPackResponse))
        }.catch { throwable ->
            Napier.e("An exception occurred while trying to get jitPack artifacts", throwable)
            emit(Result.Error(throwable.toPoodleException(isMavenCentralServer = false)))
        }
    }

    private fun JsonObject.toPartialJitPackResponse(): PartialJitPackResponse {
        return PartialJitPackResponse(keys.map { JitPackArtifactCoordinates(fullIdCoordinate = it) })
    }

    private fun PartialJitPackResponse.filterRelevantResponses(enabled: Boolean, text: String): PartialJitPackResponse {
        return if (enabled)
            copy(coordinates = coordinates.filter { it.fullIdCoordinate.split(":")[1].contains(text, ignoreCase = true) })
        else
            this
    }

    private suspend fun PartialJitPackResponse.toFullResponse(concurrency: Int = 4): JitPackResponse = coroutineScope {
        val semaphore = Semaphore(concurrency)
        val artifacts = coordinates.map { coordinates ->
            semaphore.acquire()
            async(Dispatchers.IO.limitedParallelism(concurrency)) {
                try {
                    val groupAndArtifact = coordinates.fullIdCoordinate.split(":").let { Pair(it[0], it[1]) }
                    val (groupId, artifactName) = groupAndArtifact.first to groupAndArtifact.second
                    val response = client.get("$baseUrl/builds/$groupId/$artifactName/latestOk")
                    if (response.status.isSuccess()) {
                        val metadata = response.body<JsonObject>()
                        JitPackArtifact("$groupId:$artifactName", metadata["version"]?.jsonPrimitive?.content, metadata["time"]?.jsonPrimitive?.long)
                    } else {
                        Napier.w("$groupId:$artifactName requires authentication to get additional metadata about it")
                        JitPackArtifact("", null, null)
                    }
                } finally {
                    semaphore.release()
                }
            }
        }.awaitAll()
        JitPackResponse(artifacts.filterNot { it.groupAndArtifactIdCoordinates.isEmpty() })
    }
}

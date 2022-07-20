package com.mr3y.poodle.network.exceptions

import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import kotlinx.serialization.SerializationException

internal fun Throwable.toPoodleException(isMavenCentralServer: Boolean): PoodleException {
    return when (this) {
        is SerializationException -> DecodingException(message, cause)
        is ClientRequestException -> ClientException(message, cause)
        is ServerResponseException -> {
            if (isMavenCentralServer)
                MavenCentralServerException(message, cause)
            else
                JitPackServerException(message, cause)
        }
        else -> UnknownException(message, cause)
    }
}

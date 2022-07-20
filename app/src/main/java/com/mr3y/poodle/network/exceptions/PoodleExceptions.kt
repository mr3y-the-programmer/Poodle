package com.mr3y.poodle.network.exceptions

sealed class PoodleException : Exception()
sealed interface ServerException

data class DecodingException(override val message: String?, override val cause: Throwable?) : PoodleException()
data class ClientException(override val message: String?, override val cause: Throwable?) : PoodleException()
data class MavenCentralServerException(override val message: String?, override val cause: Throwable?) : ServerException, PoodleException()
data class JitPackServerException(override val message: String?, override val cause: Throwable?) : ServerException, PoodleException()
data class UnknownException(override val message: String?, override val cause: Throwable?) : PoodleException()

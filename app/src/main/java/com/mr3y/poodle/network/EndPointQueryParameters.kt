package com.mr3y.poodle.network

sealed interface EndPointQueryParameters {

    var groupId: String

    var text: String

    var limit: Int

    fun clearQueryParameters()
}

const val DefaultMavenCentralLimit = 200

object MavenCentralQueryParameters : EndPointQueryParameters {

    override var groupId = ""

    override var limit = DefaultMavenCentralLimit
        set(value) {
            field = if (value <= 0 || value > DefaultMavenCentralLimit) DefaultMavenCentralLimit else value
        }

    override var text = ""

    var packaging = ""

    var start = 0
        set(value) {
            field = value.coerceAtLeast(0)
        }

    var tags: Set<String> = emptySet()

    var containsClassSimpleName = ""

    var containsClassFullyQualifiedName = ""

    override fun clearQueryParameters() {
        text = ""
        groupId = ""
        limit = DefaultMavenCentralLimit
        packaging = ""
        tags = emptySet()
        containsClassSimpleName = ""
        containsClassFullyQualifiedName = ""
    }
}

const val DefaultJitPackLimit = 50

object JitPackQueryParameters : EndPointQueryParameters {

    override var groupId = ""

    override var text = ""

    override var limit = DefaultJitPackLimit
        set(value) {
            field = if (value <= 0) DefaultJitPackLimit else value
        }

    override fun clearQueryParameters() {
        groupId = ""
        text = ""
        limit = DefaultJitPackLimit
    }
}

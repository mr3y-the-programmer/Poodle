package com.mr3y.poodle.network

sealed interface EndPointQueryParameters {

    var groupId: String

    var text: String

    var limit: Int
}

object MavenCentralQueryParameters : EndPointQueryParameters {

    override var groupId = ""

    override var limit = 50

    override var text = ""

    var packaging = ""

    var tags: Set<String> = emptySet()

    var containsClassSimpleName = ""

    var containsClassFullyQualifiedName = ""

    fun clearQueryParameters() {
        text = ""
        groupId = ""
        limit = 50
        packaging = ""
        tags = emptySet()
        containsClassSimpleName = ""
        containsClassFullyQualifiedName = ""
    }
}

object JitPackQueryParameters : EndPointQueryParameters {

    override var groupId = ""

    override var text = ""

    override var limit = 50
}

package com.mr3y.poodle.network

sealed interface EndPointQueryParameters {

    var groupId: String

    var text: String

    var limit: Int

    fun clearQueryParameters()
}

object MavenCentralQueryParameters : EndPointQueryParameters {

    override var groupId = ""

    override var limit = 200

    override var text = ""

    var packaging = ""

    var tags: Set<String> = emptySet()

    var containsClassSimpleName = ""

    var containsClassFullyQualifiedName = ""

    override fun clearQueryParameters() {
        text = ""
        groupId = ""
        limit = 200
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

    override fun clearQueryParameters() {
        groupId = ""
        text = ""
        limit = 50
    }
}

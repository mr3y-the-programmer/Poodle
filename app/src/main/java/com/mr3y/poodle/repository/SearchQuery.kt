package com.mr3y.poodle.repository

data class SearchQuery(
    val text: String,
    val groupId: String,
    val limit: Int,
    val packaging: String,
    val tags: Set<String>,
    val containsClassSimpleName: String,
    val containsClassFullyQualifiedName: String
) {
    companion object {
        val EMPTY = SearchQuery("", "", 0, "", emptySet(), "", "")
    }
}

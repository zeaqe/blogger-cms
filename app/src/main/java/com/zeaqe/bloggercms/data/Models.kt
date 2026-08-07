// Models.kt
package com.zeaqe.bloggercms.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Blog(
    val id: String,
    val name: String,
    val url: String
)

@Serializable
data class Post(
    val id: String? = null,
    val title: String,
    val content: String,
    val url: String? = null,
    val labels: List<String>? = null,
    val published: String? = null, // ISO8601 format for scheduling
    val status: String = "DRAFT" // DRAFT or LIVE
)

@Serializable
data class PostList(
    val items: List<Post>? = null
)


// BloggerApiService.kt
package com.zeaqe.bloggercms.data

import retrofit2.http.*

interface BloggerApiService {
    @GET("blogs/{blogId}/posts")
    suspend fun listPosts(
        @Path("blogId") blogId: String,
        @Header("Authorization") auth: String,
        @Query("labels") labels: String? = null,
        @Query("status") status: String? = null,
        @Query("q") query: String? = null
    ): PostList

    @POST("blogs/{blogId}/posts")
    suspend fun insertPost(
        @Path("blogId") blogId: String,
        @Header("Authorization") auth: String,
        @Query("isDraft") isDraft: Boolean,
        @Body post: Post
    ): Post

    @PATCH("blogs/{blogId}/posts/{postId}")
    suspend fun updatePost(
        @Path("blogId") blogId: String,
        @Path("postId") postId: String,
        @Header("Authorization") auth: String,
        @Body post: Post,
        @Query("revert") revert: Boolean? = null
    ): Post

    @DELETE("blogs/{blogId}/posts/{postId}")
    suspend fun deletePost(
        @Path("blogId") blogId: String,
        @Path("postId") postId: String,
        @Header("Authorization") auth: String
    )

    // Pages API (Similar to Posts)
    @GET("blogs/{blogId}/pages")
    suspend fun listPages(
        @Path("blogId") blogId: String,
        @Header("Authorization") auth: String
    ): PostList
}
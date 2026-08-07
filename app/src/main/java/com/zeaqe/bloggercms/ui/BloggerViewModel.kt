package com.zeaqe.bloggercms.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.zeaqe.bloggercms.data.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class BloggerViewModel(application: Application) : AndroidViewModel(application) {
    private val blogId = "YOUR_BLOG_ID" 
    private val authToken = "YOUR_OAUTH_TOKEN"
    private val authHeader = "Bearer $authToken"

    private val api = RetrofitClient.createService()
    private val db = DatabaseProvider.getInstance(application)

    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts: StateFlow<List<Post>> = _posts

    private val _pages = MutableStateFlow<List<Post>>(emptyList())
    val pages: StateFlow<List<Post>> = _pages

    val history = db.historyDao().getAllHistory()

    val searchQuery = MutableStateFlow("")
    val statusFilter = MutableStateFlow<String?>(null)

    fun onSearchQueryChanged(query: String) { searchQuery.value = query }
    fun onStatusFilterChanged(status: String?) { statusFilter.value = status }

    fun fetchPosts() {
        viewModelScope.launch {
            try {
                val response = api.listPosts(blogId, authHeader, null, statusFilter.value, searchQuery.value.ifEmpty { null })
                _posts.value = response.items ?: emptyList()
            } catch (e: Exception) { }
        }
    }

    fun fetchPages() {
        viewModelScope.launch {
            try {
                val response = api.listPages(blogId, authHeader)
                _pages.value = response.items ?: emptyList()
            } catch (e: Exception) { }
        }
    }

    fun togglePostStatus(post: Post) {
        viewModelScope.launch {
            try {
                val newStatus = if (post.status == "LIVE") "DRAFT" else "LIVE"
                val updatedPost = post.copy(status = newStatus)
                api.updatePost(blogId, post.id ?: "", authHeader, updatedPost, revert = (newStatus == "DRAFT"))
                db.historyDao().insertHistory(HistoryEntity(action = "Status changed to $newStatus", postId = post.id))
                fetchPosts()
            } catch (e: Exception) { }
        }
    }

    fun deletePost(postId: String) {
        viewModelScope.launch {
            try {
                api.deletePost(blogId, postId, authHeader)
                db.historyDao().insertHistory(HistoryEntity(action = "Deleted Post", postId = postId))
                fetchPosts()
            } catch (e: Exception) { }
        }
    }

    fun savePost(postId: String?, title: String, labels: String, customUrl: String, scheduleEnabled: Boolean, scheduleTime: String, content: String, isPublish: Boolean) {
        viewModelScope.launch {
            try {
                val finalUrl = if (customUrl.isEmpty()) title.lowercase().replace(" ", "-").replace(Regex("[^a-z0-9-]"), "") else customUrl
                val finalStatus = if (scheduleEnabled) "SCHEDULED" else if (isPublish) "LIVE" else "DRAFT"
                val post = Post(
                    id = postId, title = title, content = content, url = finalUrl,
                    labels = labels.split(",").map { it.trim() }.filter { it.isNotEmpty() },
                    published = if (scheduleEnabled) scheduleTime else null, status = finalStatus
                )

                if (postId == null || postId == "new") {
                    api.insertPost(blogId, authHeader, isDraft = !isPublish && !scheduleEnabled, post)
                    db.historyDao().insertHistory(HistoryEntity(action = "Created Post (${post.status})", postId = null))
                } else {
                    api.updatePost(blogId, postId, authHeader, post, revert = false)
                    db.historyDao().insertHistory(HistoryEntity(action = "Updated Post (${post.status})", postId = postId))
                }
            } catch (e: Exception) { }
        }
    }
}
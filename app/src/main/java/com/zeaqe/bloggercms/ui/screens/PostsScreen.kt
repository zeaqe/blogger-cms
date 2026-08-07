package com.zeaqe.bloggercms.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Publish
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.zeaqe.bloggercms.ui.BloggerViewModel

@Composable
fun PostsScreen(navController: NavController, viewModel: BloggerViewModel = viewModel()) {
    val posts by viewModel.posts.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val statusFilter by viewModel.statusFilter.collectAsState()

    LaunchedEffect(Unit) { viewModel.fetchPosts() }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("editor/new") }) {
                Icon(Icons.Default.Add, contentDescription = "New Post")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.onSearchQueryChanged(it) },
                label = { Text("Search posts") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                FilterChip(selected = statusFilter == "LIVE", onClick = { viewModel.onStatusFilterChanged("LIVE") }, label = { Text("Published") })
                Spacer(modifier = Modifier.width(8.dp))
                FilterChip(selected = statusFilter == "DRAFT", onClick = { viewModel.onStatusFilterChanged("DRAFT") }, label = { Text("Drafts") })
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = { viewModel.fetchPosts() }) { Text("Apply") }
            }
            Spacer(modifier = Modifier.height(16.dp))
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(posts) { post ->
                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), elevation = CardDefaults.cardElevation(2.dp)) {
                        Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(post.title, style = MaterialTheme.typography.titleMedium)
                                Text("Status: ${post.status}", style = MaterialTheme.typography.bodySmall)
                            }
                            IconButton(onClick = { viewModel.togglePostStatus(post) }) {
                                Icon(if (post.status == "LIVE") Icons.Default.Edit else Icons.Default.Publish, contentDescription = "Toggle Status")
                            }
                            IconButton(onClick = { viewModel.deletePost(post.id ?: "") }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete")
                            }
                        }
                    }
                }
            }
        }
    }
}
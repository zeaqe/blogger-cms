package com.zeaqe.bloggercms.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen(viewModel: BloggerViewModel = viewModel()) {
    val history by viewModel.history.collectAsState(initial = emptyList())

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Local Action History", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))
        
        LazyColumn {
            items(history) { item ->
                val date = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(Date(item.timestamp))
                ListItem(
                    headlineContent = { Text(item.action) },
                    supportingContent = { Text("Post ID: ${item.postId ?: "N/A"}") },
                    trailingContent = { Text(date, style = MaterialTheme.typography.bodySmall) }
                )
                Divider()
            }
        }
    }
}
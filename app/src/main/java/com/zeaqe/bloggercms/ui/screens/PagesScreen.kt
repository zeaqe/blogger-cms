package com.zeaqe.bloggercms.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.zeaqe.bloggercms.ui.BloggerViewModel

@Composable
fun PagesScreen(navController: NavController, viewModel: BloggerViewModel = viewModel()) {
    val pages by viewModel.pages.collectAsState()
    LaunchedEffect(Unit) { viewModel.fetchPages() }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Static Pages", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn {
            items(pages) { page ->
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(page.title, style = MaterialTheme.typography.titleMedium)
                        Text("URL: ${page.url ?: "N/A"}", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}
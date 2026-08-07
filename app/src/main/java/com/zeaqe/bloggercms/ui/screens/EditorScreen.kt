package com.example.bloggercms.ui.screens

import android.webkit.JavascriptInterface
import android.webkit.WebView
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.bloggercms.ui.BloggerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorScreen(
    navController: NavController,
    postId: String?,
    viewModel: BloggerViewModel = viewModel()
) {
    var title by remember { mutableStateOf("") }
    var labels by remember { mutableStateOf("") }
    var customUrl by remember { mutableStateOf("") }
    var scheduleEnabled by remember { mutableStateOf(false) }
    var scheduleTime by remember { mutableStateOf("") }
    
    val webViewRef = remember { mutableStateOf<WebView?>(null) }

    // 1. IMPORTHOLDER CHECK: When screen opens, check if we came from ImportScreen
    LaunchedEffect(Unit) {
        if (ImportHolder.content.isNotEmpty()) {
            val importedContent = ImportHolder.content
            // Wait for WebView to finish loading, then inject content
            webViewRef.value?.postDelayed({
                // Escape quotes for safe JS injection
                val safeContent = importedContent.replace("\\", "\\\\").replace("'", "\\'").replace("\n", "\\n")
                webViewRef.value?.evaluateJavascript("setContent('$safeContent');", null)
            }, 500) // Half-second delay to ensure CodeMirror is initialized
            
            // Clear the holder so it doesn't inject again if the user rotates screen
            ImportHolder.content = ""
        }
    }

    Scaffold(
        topBar = { 
            TopAppBar(title = { Text(if (postId == null || postId == "new") "New Post" else "Edit Post") }) 
        },
        bottomBar = {
            BottomAppBar {
                // 2. SAVE DRAFT BUTTON
                TextButton(onClick = {
                    // Evaluate Javascript to get content from CodeMirror first
                    webViewRef.value?.evaluateJavascript("getContent();") { result ->
                        // Result comes back wrapped in quotes as a JSON string (e.g., "\"<p>HTML</p>\"")
                        val content = result?.trim('"')?.replace("\\n", "\n")?.replace("\\\"", "\"") ?: ""
                        
                        viewModel.savePost(
                            postId = if (postId == "new") null else postId,
                            title = title,
                            labels = labels,
                            customUrl = customUrl,
                            scheduleEnabled = false,
                            scheduleTime = "",
                            content = content,
                            isPublish = false // false = DRAFT
                        )
                        navController.popBackStack()
                    }
                }) { Text("Save Draft") }
                
                Spacer(Modifier.weight(1f))
                
                // 3. PUBLISH/SCHEDULE BUTTON
                Button(onClick = {
                    webViewRef.value?.evaluateJavascript("getContent();") { result ->
                        val content = result?.trim('"')?.replace("\\n", "\n")?.replace("\\\"", "\"") ?: ""
                        
                        viewModel.savePost(
                            postId = if (postId == "new") null else postId,
                            title = title,
                            labels = labels,
                            customUrl = customUrl,
                            scheduleEnabled = scheduleEnabled,
                            scheduleTime = if (scheduleEnabled) scheduleTime else "",
                            content = content,
                            isPublish = true // true = LIVE or SCHEDULED
                        )
                        navController.popBackStack()
                    }
                }) { 
                    Text(if (scheduleEnabled) "Schedule Post" else "Publish") 
                }
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            OutlinedTextField(
                value = title, 
                onValueChange = { title = it }, 
                label = { Text("Title") }, 
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            
            OutlinedTextField(
                value = labels, 
                onValueChange = { labels = it }, 
                label = { Text("Labels (comma separated)") }, 
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            
            OutlinedTextField(
                value = customUrl, 
                onValueChange = { customUrl = it }, 
                label = { Text("Custom URL (optional)") }, 
                modifier = Modifier.fillMaxWidth(),
                supportingText = { Text("Leave empty to auto-generate from title") }
            )
            
            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                Text("Schedule Post")
                Spacer(Modifier.weight(1f))
                Switch(checked = scheduleEnabled, onValueChange = { scheduleEnabled = it })
            }
            
            if (scheduleEnabled) {
                OutlinedTextField(
                    value = scheduleTime, 
                    onValueChange = { scheduleTime = it }, 
                    label = { Text("ISO8601 Time (e.g. 2023-12-31T10:00:00Z)") }, 
                    modifier = Modifier.fillMaxWidth()
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // CodeMirror WebView
            AndroidView(
                factory = { context ->
                    WebView(context).apply {
                        settings.javaScriptEnabled = true
                        loadUrl("file:///android_asset/codemirror.html")
                        webViewRef.value = this
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
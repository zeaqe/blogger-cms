package com.zeaqe.bloggercms.ui.screens

import android.webkit.WebView
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.zeaqe.bloggercms.data.ImportHolder
import com.zeaqe.bloggercms.ui.BloggerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorScreen(navController: NavController, postId: String?, viewModel: BloggerViewModel = viewModel()) {
    var title by remember { mutableStateOf("") }
    var labels by remember { mutableStateOf("") }
    var customUrl by remember { mutableStateOf("") }
    var scheduleEnabled by remember { mutableStateOf(false) }
    var scheduleTime by remember { mutableStateOf("") }
    val webViewRef = remember { mutableStateOf<WebView?>(null) }

    LaunchedEffect(Unit) {
        if (ImportHolder.content.isNotEmpty()) {
            val importedContent = ImportHolder.content
            webViewRef.value?.postDelayed({
                val safeContent = importedContent.replace("\\", "\\\\").replace("'", "\\'").replace("\n", "\\n")
                webViewRef.value?.evaluateJavascript("setContent('$safeContent');", null)
            }, 500)
            ImportHolder.content = ""
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text(if (postId == null || postId == "new") "New Post" else "Edit Post") }) },
        bottomBar = {
            BottomAppBar {
                TextButton(onClick = {
                    webViewRef.value?.evaluateJavascript("getContent();") { result ->
                        val content = result?.trim('"')?.replace("\\n", "\n")?.replace("\\\"", "\"") ?: ""
                        viewModel.savePost(if (postId == "new") null else postId, title, labels, customUrl, false, "", content, false)
                        navController.popBackStack()
                    }
                }) { Text("Save Draft") }
                Spacer(Modifier.weight(1f))
                Button(onClick = {
                    webViewRef.value?.evaluateJavascript("getContent();") { result ->
                        val content = result?.trim('"')?.replace("\\n", "\n")?.replace("\\\"", "\"") ?: ""
                        viewModel.savePost(if (postId == "new") null else postId, title, labels, customUrl, scheduleEnabled, scheduleTime, content, true)
                        navController.popBackStack()
                    }
                }) { Text(if (scheduleEnabled) "Schedule Post" else "Publish") }
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = labels, onValueChange = { labels = it }, label = { Text("Labels (comma separated)") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = customUrl, onValueChange = { customUrl = it }, label = { Text("Custom URL (optional)") }, modifier = Modifier.fillMaxWidth())
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Schedule Post")
                Spacer(Modifier.weight(1f))
                Switch(checked = scheduleEnabled, onCheckedChange = { scheduleEnabled = it })
            }
            
            if (scheduleEnabled) {
                OutlinedTextField(value = scheduleTime, onValueChange = { scheduleTime = it }, label = { Text("ISO8601 Time") }, modifier = Modifier.fillMaxWidth())
            }
            
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
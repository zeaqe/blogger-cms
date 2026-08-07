package com.zeaqe.bloggercms.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.zeaqe.bloggercms.data.ImportHolder // A simple object to pass data between screens

@Composable
fun ImportScreen(navController: NavController) {
    val context = LocalContext.current
    var importedText by remember { mutableStateOf("") }
    var fileName by remember { mutableStateOf("") }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            context.contentResolver.openInputStream(it)?.bufferedReader()?.use { reader ->
                importedText = reader.readText()
                fileName = it.lastPathSegment ?: "unknown"
            }
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Bulk Import Articles", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Import HTML or JSON files. The content will load into the editor for review before publishing.")
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(onClick = { launcher.launch("*/*") }) {
            Text("Select HTML/JSON File")
        }

        Spacer(modifier = Modifier.height(16.dp))
        
        if (importedText.isNotEmpty()) {
            Text("File loaded: $fileName")
            Spacer(modifier = Modifier.height(8.dp))
            
            // Preview the first 200 characters
            Text("Preview: ${importedText.take(200)}...", style = MaterialTheme.typography.bodySmall)
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(onClick = {
                ImportHolder.content = importedText
                navController.navigate("editor/new")
            }) {
                Text("Load into Editor for Review")
            }
        }
    }
}

// Add this object somewhere in your project (e.g., in data package)
object ImportHolder {
    var content: String = ""
}
package com.zeaqe.bloggercms.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

val Context.dataStore by preferencesDataStore(name = "settings")

@Composable
fun SettingsScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val BLOG_ID = stringPreferencesKey("blog_id")
    val AUTH_TOKEN = stringPreferencesKey("auth_token")

    val blogId by context.dataStore.data.map { it[BLOG_ID] ?: "" }.collectAsState(initial = "")
    val authToken by context.dataStore.data.map { it[AUTH_TOKEN] ?: "" }.collectAsState(initial = "")

    var tempBlogId by remember { mutableStateOf(blogId) }
    var tempAuthToken by remember { mutableStateOf(authToken) }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Blogger Configuration", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = tempBlogId, onValueChange = { tempBlogId = it }, label = { Text("Blogger Blog ID") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = tempAuthToken, onValueChange = { tempAuthToken = it }, label = { Text("OAuth 2.0 Access Token") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            scope.launch {
                context.dataStore.edit { settings ->
                    settings[BLOG_ID] = tempBlogId
                    settings[AUTH_TOKEN] = tempAuthToken
                }
            }
        }) { Text("Save Settings") }
    }
}
package com.zeaqe.bloggercms.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.zeaqe.bloggercms.ui.screens.*
import kotlinx.coroutines.launch

data class MenuItem(val title: String, val icon: ImageVector, val route: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BloggerCMSApp() {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val menuItems = listOf(
        MenuItem("Posts", Icons.Default.Article, "posts"),
        MenuItem("Pages", Icons.Default.Web, "pages"),
        MenuItem("Import", Icons.Default.Upload, "import"),
        MenuItem("Live WebView", Icons.Default.Public, "webview"),
        MenuItem("History", Icons.Default.History, "history"),
        MenuItem("Settings", Icons.Default.Settings, "settings")
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text("Blogger CMS", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.headlineMedium)
                Divider()
                menuItems.forEach { item ->
                    NavigationDrawerItem(
                        label = { Text(item.title) },
                        icon = { Icon(item.icon, contentDescription = item.title) },
                        selected = false,
                        onClick = {
                            navController.navigate(item.route) {
                                launchSingleTop = true
                            }
                            scope.launch { drawerState.close() }
                        },
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Blogger CMS") },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    }
                )
            }
        ) { padding ->
            NavHost(
                navController = navController,
                startDestination = "posts",
                modifier = Modifier.padding(padding)
            ) {
                composable("posts") { PostsScreen(navController) }
                composable("editor/{postId}") { backStackEntry ->
                    EditorScreen(navController, backStackEntry.arguments?.getString("postId"))
                }
                composable("pages") { PagesScreen(navController) }
                composable("import") { ImportScreen(navController) }
                composable("webview") { BloggerWebViewScreen() }
                composable("history") { HistoryScreen() }
                composable("settings") { SettingsScreen() }
            }
        }
    }
}
// MainActivity.kt
package com.zeaqe.bloggercms

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import com.zeaqe.bloggercms.ui.BloggerCMSApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                BloggerCMSApp()
            }
        }
    }
}
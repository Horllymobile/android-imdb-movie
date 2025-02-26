package com.horllymobile.androidmoview

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Surface
import com.horllymobile.androidmoview.ui.MainUI
import com.horllymobile.androidmoview.ui.theme.AndroidMovieTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AndroidMovieTheme {
                Surface() {
                    MainUI()
                }
            }
        }
    }
}

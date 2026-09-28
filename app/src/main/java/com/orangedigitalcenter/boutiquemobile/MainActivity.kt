package com.orangedigitalcenter.boutiquemobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.orangedigitalcenter.boutiquemobile.ui.navigation.BoutiqueApp
import com.orangedigitalcenter.boutiquemobile.ui.theme.BoutiqueTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BoutiqueTheme {
                BoutiqueApp()
            }
        }
    }
}

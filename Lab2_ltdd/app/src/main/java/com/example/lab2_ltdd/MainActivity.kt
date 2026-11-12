package com.example.lab2_ltdd

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.lab2_ltdd.ui.BusinessCardScreen
import com.example.lab2_ltdd.ui.theme.Lab2_ltddTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Lab2_ltddTheme {
                BusinessCardScreen()
            }
        }
    }
}

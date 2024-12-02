package edu.kdmk.greengrocer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import edu.kdmk.greengrocer.ui.screens.MainScreen
import edu.kdmk.greengrocer.ui.theme.GreenGrocerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GreenGrocerTheme {
                MainScreen()
            }
        }
    }
}

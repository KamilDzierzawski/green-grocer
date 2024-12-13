package edu.kdmk.greengrocer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import edu.kdmk.greengrocer.ui.view.screen.AuthScreen
import edu.kdmk.greengrocer.ui.view.theme.GreenGrocerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GreenGrocerTheme {
                AuthScreen()
            }
        }
    }
}

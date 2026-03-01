package dev.danielk.cluein

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import dev.danielk.cluein.ui.theme.ClueinTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ClueinTheme {
                val navController = rememberNavController()
                ClueinNavGraph(navController)
            }
        }
    }
}

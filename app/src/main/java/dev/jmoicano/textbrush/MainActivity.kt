package dev.jmoicano.textbrush

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import dagger.hilt.android.AndroidEntryPoint
import dev.jmoicano.textbrush.ui.BrushComponent
import dev.jmoicano.textbrush.ui.TextBrushViewModel
import dev.jmoicano.textbrush.ui.theme.TextBrushTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: TextBrushViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TextBrushTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier
                        .fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    BrushComponent(viewModel)
                }
            }
        }
    }
}
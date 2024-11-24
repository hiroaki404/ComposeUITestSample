package com.example.composeuitestsample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.composeuitestsample.ui.SampleButtonScreen
import com.example.composeuitestsample.ui.SampleScreen
import com.example.composeuitestsample.ui.theme.ComposeUITestSampleTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComposeUITestSampleTheme {
                SampleScreen()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ComposeUITestSampleTheme {
        SampleButtonScreen()
    }
}

package com.example.composeuitestsample.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun SampleScreen(modifier: Modifier = Modifier) {
    Scaffold(modifier = modifier.fillMaxSize()) { innerPadding ->
        Button(modifier = Modifier.padding(innerPadding), onClick = {}) {
            Text(modifier = Modifier.testTag("sample"), text = "sample")
            Text(text = "text")
            Icon(
                imageVector = Icons.Filled.Favorite,
                contentDescription = "favorite"
            )
        }
    }
}

@Preview
@Composable
private fun SampleScreenPreview() {
    SampleScreen()
}

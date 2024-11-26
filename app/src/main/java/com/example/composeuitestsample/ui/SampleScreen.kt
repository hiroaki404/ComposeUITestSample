package com.example.composeuitestsample.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SampleScreen(modifier: Modifier = Modifier) {
    Scaffold(modifier.fillMaxSize()) { innerPadding ->
        SampleScreenContent(modifier.padding(innerPadding))
    }
}

@Composable
fun SampleScreenContent(modifier: Modifier = Modifier) {
    Row(modifier = modifier) {
        Text(text = "sample")
        Text(text = "text")
        Icon(
            imageVector = Icons.Filled.Favorite,
            contentDescription = "favorite"
        )
    }
}

@Composable
fun SampleButtonScreen(modifier: Modifier = Modifier) {
    Scaffold(modifier = modifier.fillMaxSize()) { innerPadding ->
        Button(modifier = Modifier
            .testTag("button")
            .padding(innerPadding), onClick = {}) {
            Text(
                modifier = Modifier
                    .semantics { testTag = "sample" }
                    .testTag("sample"), text = "sample"
            )
            Text(text = "text")
            Icon(
                imageVector = Icons.Filled.Favorite,
                contentDescription = "favorite"
            )
        }
    }
}

@Composable
fun SampleWithDialogScreen(modifier: Modifier = Modifier) {
    Scaffold(modifier = modifier.fillMaxSize()) { innerPadding ->
        var showDialog by remember { mutableStateOf(false) }
        Button(modifier = Modifier.padding(innerPadding), onClick = {
            showDialog = true
        }) {
            Text(text = "show dialog")
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text(text = "title") },
                text = { Text(text = "text") },
                confirmButton = {
                    Button(onClick = { showDialog = false }) {
                        Text(text = "confirm")
                    }
                },
                dismissButton = {
                    Button(onClick = { showDialog = false }) {
                        Text(text = "dismiss")
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SampleWithSheetScreen(modifier: Modifier = Modifier) {
    Scaffold(modifier = modifier.fillMaxSize()) { innerPadding ->
        val scaffoldState = rememberBottomSheetScaffoldState()
        BottomSheetScaffold(
            modifier = Modifier.padding(innerPadding),
            scaffoldState = scaffoldState,
            sheetContent = {
                Text(text = "sheet content")
            },
            sheetPeekHeight = 0.dp,
        ) {
            val scope = rememberCoroutineScope()
            Button(modifier = Modifier, onClick = {
                scope.launch {
                    scaffoldState.bottomSheetState.expand()
                }
            }) {
                Text(text = "show sheet")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SamplePullToRefresh(modifier: Modifier = Modifier, coroutineContext: CoroutineContext) {
    var isRefreshing by remember { mutableStateOf(false) }

    Scaffold(modifier = modifier) { innerPadding ->
        PullToRefreshBox(
            modifier = Modifier.padding(innerPadding),
            isRefreshing = isRefreshing,
            onRefresh = {
                isRefreshing = true
                CoroutineScope(coroutineContext).launch {
                    delay(2000)
                    isRefreshing = false
                }
            },
        ) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "sample")
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun SampleScreenPreview() {
    SampleButtonScreen()
}

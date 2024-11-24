package com.example.composeuitestsample

import android.os.Looper
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.printToLog
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertFalse
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun simple_compose_test() {
        composeTestRule.setContent {
            Column {
                Text("sample")
                Button(onClick = {}) {
                    Text("Button")
                }
            }
        }
        composeTestRule.onNode(hasText("sample"))
            .assertIsDisplayed()
        composeTestRule.onNode(hasText("Button"))
            .performClick()
    }

    @Test
    fun print_log() {
//        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
//        assertEquals("com.example.composeuitestsample", appContext.packageName)
        composeTestRule.setContent {
            Text("Hello, World!")
        }
        composeTestRule.onRoot()
            .printToLog("test")

    }

    @Test
    fun verify_thread() {
        Log.d("test", Thread.currentThread().name) // androidx.test.runner.AndroidJUnitRunner
        assertFalse(Thread.currentThread() == Looper.getMainLooper().thread) // false, because this is a test thread in instrumented test
    }
}

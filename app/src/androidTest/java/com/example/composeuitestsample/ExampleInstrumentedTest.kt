package com.example.composeuitestsample

import android.util.Log
import androidx.compose.material3.Text
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.printToLog
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun useAppContext() {
        composeTestRule.setContent {
            Text("Hello, World!")
        }
        composeTestRule.onRoot()
            .printToLog("test")

        Log.d("test", "${Thread.currentThread()}")
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.example.composeuitestsample", appContext.packageName)
    }
}

package com.example.composeuitestsample

import android.os.Looper
import android.os.SystemClock
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isRoot
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.printToString
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.composeuitestsample.ui.SampleButtonScreen
import com.example.composeuitestsample.ui.SampleScreenContent
import com.example.composeuitestsample.ui.SampleWithDialogScreen
import com.example.composeuitestsample.ui.SampleWithSheetScreen
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import java.time.Duration
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import org.robolectric.shadows.ShadowSystemClock

@RunWith(AndroidJUnit4::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class SampleScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Config(qualifiers = RobolectricDeviceQualifiers.Pixel7)
    @Test
    fun verify_thread() {
        println(Thread.currentThread()) // Thread[#33,SDK 34 Main Thread,5,SDK 34]
        println(Thread.currentThread().name) // SDK 34 Main Thread
        assertTrue(Thread.currentThread() == Looper.getMainLooper().thread) // true, because this is a test thread in robolectric
    }

    @Config(qualifiers = RobolectricDeviceQualifiers.Pixel7)
    @Test
    fun verify_coroutine_thread() = runTest {
        println(Thread.currentThread()) // Thread[#33,SDK 34 Main Thread @kotlinx.coroutines.test runner#3,5,SDK 34]
        println(Thread.currentThread().name) // SDK 34 Main Thread @kotlinx.coroutines.test runner#3
        assertTrue(Thread.currentThread() == Looper.getMainLooper().thread) // true, because this is a test thread in robolectric
    }

    @Config(qualifiers = RobolectricDeviceQualifiers.Pixel7)
    @Test
    fun verify_coroutine_test() = runTest {
        launch {
            delay(1000)
            println("coroutine")
        }
        println("test")

    }

    @Config(qualifiers = RobolectricDeviceQualifiers.Pixel7)
    @Test
    fun advance_system_clock() {
        println(SystemClock.uptimeMillis())
        ShadowSystemClock.advanceBy(Duration.ofHours(1))
        println(SystemClock.uptimeMillis())
        // advanced SystemClock, but not advanced System.currentTimeMillis()
    }

    @Config(qualifiers = RobolectricDeviceQualifiers.Pixel7)
    @Test
    fun verify_sample_tree() {
        composeTestRule.setContent {
            SampleScreenContent()
        }
        println(composeTestRule.onRoot().printToString()) // userUnmergedTree = false by default

        println(composeTestRule.onRoot(useUnmergedTree = true).printToString())

        composeTestRule.onNode(hasText("sample"))
            .assertIsDisplayed()
    }

    @Config(qualifiers = RobolectricDeviceQualifiers.Pixel7)
    @Test
    fun verify_merged_tree() {
        composeTestRule.setContent {
            SampleButtonScreen()
        }
        println(composeTestRule.onRoot().printToString()) // userUnmergedTree = false by default
        println(composeTestRule.onRoot(useUnmergedTree = true).printToString())

        // ok
        composeTestRule.onNode(hasText("sample"), useUnmergedTree = true)
            .performClick()
        // ok
        composeTestRule.onNode(hasText("sample"), useUnmergedTree = false)
            .performClick()

        // NG, deleted child testTag in mergedTree
//        composeTestRule.onNode(hasTestTag("sample"))
//            .assertIsDisplayed()
//        composeTestRule.onNode(hasTestTag("sample"))
//            .performClick()

        // ok
        composeTestRule.onNode(hasTestTag("sample"), useUnmergedTree = true)
            .performClick()
    }

    @Config(qualifiers = RobolectricDeviceQualifiers.Pixel7)
    @Test
    fun verify_with_dialog_tree() {
        composeTestRule.setContent {
            SampleWithDialogScreen()
        }
        composeTestRule.onNode(hasText("show dialog"))
            .performClick()
        // ng
//        println(
//            composeTestRule.onRoot()
//                .printToString()
//        )

        // get dialog tree
        println(
            composeTestRule.onAllNodes(isRoot())[1]
                .printToString()
        )
    }

    @Config(qualifiers = RobolectricDeviceQualifiers.Pixel7)
    @Test
    fun verify_with_sheet_tree() {
        composeTestRule.setContent {
            SampleWithSheetScreen()
        }
        composeTestRule.onNode(hasText("show sheet"))
            .performClick()
        composeTestRule.onRoot()
            .also { println(it.printToString()) }
    }
}

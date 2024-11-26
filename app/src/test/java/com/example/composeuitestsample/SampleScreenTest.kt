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
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.printToString
import androidx.compose.ui.test.swipeDown
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.composeuitestsample.ui.SampleButtonScreen
import com.example.composeuitestsample.ui.SamplePullToRefresh
import com.example.composeuitestsample.ui.SampleScreenContent
import com.example.composeuitestsample.ui.SampleWithDialogScreen
import com.example.composeuitestsample.ui.SampleWithSheetScreen
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import java.time.Duration
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.rules.TestRule
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import org.junit.runner.RunWith
import org.junit.runners.model.Statement
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import org.robolectric.shadows.ShadowSystemClock

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
@Config(application = HiltTestApplication::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class SampleScreenTest {
    class HiltInjectRule(private val rule: HiltAndroidRule) : TestWatcher() {
        override fun starting(description: Description?) {
            super.starting(description)
            rule.inject()
        }
    }

    class HiltAndComposeRule(private val testInstance: Any) : TestRule {
        val composeRule = createComposeRule()
        override fun apply(base: Statement?, description: Description?): Statement {
            val hiltAndroidRule = HiltAndroidRule(testInstance)
            return RuleChain.outerRule(hiltAndroidRule)
                .around(HiltInjectRule(hiltAndroidRule))
                .around(composeRule)
                .apply(base, description)
        }

    }

    @get:Rule
    val rule: HiltAndComposeRule = HiltAndComposeRule(this)

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
        rule.composeRule.setContent {
            SampleScreenContent()
        }
        println(rule.composeRule.onRoot().printToString()) // userUnmergedTree = false by default

        println(rule.composeRule.onRoot(useUnmergedTree = true).printToString())

        rule.composeRule.onNode(hasText("sample"))
            .assertIsDisplayed()
    }

    @Config(qualifiers = RobolectricDeviceQualifiers.Pixel7)
    @Test
    fun verify_merged_tree() {
        rule.composeRule.setContent {
            SampleButtonScreen()
        }
        println(rule.composeRule.onRoot().printToString()) // userUnmergedTree = false by default
        println(rule.composeRule.onRoot(useUnmergedTree = true).printToString())

        // ok
        rule.composeRule.onNode(hasText("sample"), useUnmergedTree = true)
            .performClick()
        // ok
        rule.composeRule.onNode(hasText("sample"), useUnmergedTree = false)
            .performClick()

        // NG, deleted child testTag in mergedTree
//        rule.composeRule.onNode(hasTestTag("sample"))
//            .assertIsDisplayed()
//        rule.composeRule.onNode(hasTestTag("sample"))
//            .performClick()

        // ok
        rule.composeRule.onNode(hasTestTag("sample"), useUnmergedTree = true)
            .performClick()
    }

    @Config(qualifiers = RobolectricDeviceQualifiers.Pixel7)
    @Test
    fun verify_with_dialog_tree() {
        rule.composeRule.setContent {
            SampleWithDialogScreen()
        }
        rule.composeRule.onNode(hasText("show dialog"))
            .performClick()
        // ng
//        println(
//            rule.composeRule.onRoot()
//                .printToString()
//        )

        // get dialog tree
        println(
            rule.composeRule.onAllNodes(isRoot())[1]
                .printToString()
        )
    }

    @Config(qualifiers = RobolectricDeviceQualifiers.Pixel7)
    @Test
    fun verify_with_sheet_tree() {
        rule.composeRule.setContent {
            SampleWithSheetScreen()
        }
        rule.composeRule.onNode(hasText("show sheet"))
            .performClick()
        rule.composeRule.onRoot()
            .also { println(it.printToString()) }
    }

    @Config(qualifiers = RobolectricDeviceQualifiers.Pixel7)
    @Test
    fun verify_pull_to_refresh() = runTest {
        rule.composeRule.setContent {
            SamplePullToRefresh(coroutineContext = this@runTest.coroutineContext)
        }

        // refresh前
        rule.composeRule.also {
            println(it.onRoot().printToString())
        } // 見えてなくてもpull to refreshはnodeにある
        rule.composeRule.onRoot()
            .captureRoboImage()

        // pull to refresh
        rule.composeRule
            .onRoot()
            .performTouchInput {
                swipeDown(
                    startY = visibleSize.height * 2 / 5F,
                    endY = visibleSize.height * 4 / 5F,
                )
            }

        // refresh直後
        rule.composeRule.also {
            println(it.onRoot().printToString())
        }
        rule.composeRule.onRoot()
            .captureRoboImage()

        // refresh状態でなくなった時
        this.testScheduler.advanceUntilIdle()
        rule.composeRule.waitForIdle()
        rule.composeRule.also {
            println(it.onRoot().printToString())
        }
        rule.composeRule.onRoot()
            .captureRoboImage()
    }
}

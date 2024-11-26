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
import com.example.composeuitestsample.data.SampleRepository
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import javax.inject.Inject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.rules.TestRule
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import org.junit.runner.RunWith
import org.junit.runners.model.Statement

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
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

    @Inject
    lateinit var sampleRepository: SampleRepository

    @Test
    fun simple_compose_test() {
        rule.composeRule.setContent {
            Column {
                Text("sample")
                Button(onClick = {}) {
                    Text("Button")
                }
            }
        }
        rule.composeRule.onNode(hasText("sample"))
            .assertIsDisplayed()
        rule.composeRule.onNode(hasText("Button"))
            .performClick()
    }

    @Test
    fun print_log() {
//        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
//        assertEquals("com.example.composeuitestsample", appContext.packageName)
        rule.composeRule.setContent {
            Text("Hello, World!")
        }
        rule.composeRule.onRoot()
            .printToLog("test")

    }

    @Test
    fun verify_thread() {
        Log.d("test", Thread.currentThread().name) // androidx.test.runner.AndroidJUnitRunner
        assertFalse(Thread.currentThread() == Looper.getMainLooper().thread) // false, because this is a test thread in instrumented test
    }

    @Test
    fun verify_inject() {
        assertEquals("Sample Data", sampleRepository.getSampleData())
    }
}

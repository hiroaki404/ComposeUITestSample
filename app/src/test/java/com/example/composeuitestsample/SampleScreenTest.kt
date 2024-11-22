package com.example.composeuitestsample

import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.printToString
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.composeuitestsample.ui.SampleScreen
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(AndroidJUnit4::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class SampleScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Config(qualifiers = RobolectricDeviceQualifiers.Pixel7)
    @Test
    fun verify_merged_tree() {
        composeTestRule.setContent {
            SampleScreen()
        }
        println(composeTestRule.onRoot().printToString()) // userUnmergedTree = false by default
        println(composeTestRule.onRoot(useUnmergedTree = true).printToString())

        // ok
        composeTestRule.onNode(hasText("sample"), useUnmergedTree = true)
            .performClick()
        // ok
        composeTestRule.onNode(hasText("sample"), useUnmergedTree = false)
            .performClick()
        // NG
//        composeTestRule.onNode(hasTestTag("sample"))
//            .performClick()
        composeTestRule.onNode(hasTestTag("sample"), useUnmergedTree = true)
            .performClick()

        composeTestRule.onRoot()
            .captureRoboImage()
    }
}

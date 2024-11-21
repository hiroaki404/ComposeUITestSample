package com.example.composeuitestsample

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
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
    fun show_text() {
        composeTestRule.setContent {
            SampleScreen()
        }
        composeTestRule.onNodeWithText("sample")

        composeTestRule.onRoot()
            .captureRoboImage()
    }
}

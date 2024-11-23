package com.example.composeuitestsample

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

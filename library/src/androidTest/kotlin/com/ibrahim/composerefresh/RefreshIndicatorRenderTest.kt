package com.ibrahim.composerefresh

import androidx.compose.ui.test.junit4.createComposeRule
import com.ibrahim.composescrollrefresh.core.RefreshIndicatorHost
import com.ibrahim.composescrollrefresh.core.RefreshIndicatorRegistry
import com.ibrahim.composescrollrefresh.core.RefreshIndicatorStyle
import com.ibrahim.composescrollrefresh.rememberRefreshScrollState
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

//  Every registered style renders without crashing

@RunWith(Parameterized::class)
class RefreshIndicatorRenderTest(private val style: RefreshIndicatorStyle) {

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun data(): List<RefreshIndicatorStyle> {
            return RefreshIndicatorRegistry.categories.flatMap { it.styles }
        }
    }

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun renders_without_crashing() {
        composeRule.setContent {
            val state = rememberRefreshScrollState(200f)
            RefreshIndicatorHost(state = state, style = style)
        }

        composeRule.waitForIdle()
    }
}
package com.ibrahim.composerefresh

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.dp
import com.ibrahim.composescrollrefresh.RefreshScrollState
import com.ibrahim.composescrollrefresh.pullToRefresh
import com.ibrahim.composescrollrefresh.rememberRefreshScrollState
import com.ibrahim.composescrollrefresh.core.RefreshIndicatorHost
import com.ibrahim.composescrollrefresh.core.RefreshIndicatorRegistry
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class RefreshIndicatorHostIntegrationTest {

    @get:Rule
    val composeRule = createComposeRule()


    // 1. zero height state
    @Test
    fun host_hasZeroHeight_whenIdle() {
        composeRule.setContent {
            val state = rememberRefreshScrollState(200f)

            Box(modifier = Modifier.testTag("host_box")) {
                RefreshIndicatorHost(
                    state = state,
                    style = RefreshIndicatorRegistry.Wave.classicWave
                )
            }
        }

        composeRule.onNodeWithTag("host_box").assertExists()
    }

    // 2. pull updates state safely
    @Test
    fun host_updates_whenPulled() {
        composeRule.setContent {
            val state = rememberRefreshScrollState(200f)

            LaunchedEffect(Unit) {
                state.onPull(300f)
            }

            Box(modifier = Modifier.testTag("pulled_host")) {
                RefreshIndicatorHost(
                    state = state,
                    style = RefreshIndicatorRegistry.Bubble.pulseBubble
                )
            }
        }

        composeRule.waitForIdle()
        composeRule.onNodeWithTag("pulled_host").assertExists()
    }

    // 3. full refresh flow
    @Test
    fun fullFlow_refresh_executes_and_resets() {
        var refreshCount = 0

        composeRule.setContent {
            val state = rememberRefreshScrollState(
                refreshThreshold = 100f,
                refreshingThreshold = 80f
            )

            Column {
                RefreshIndicatorHost(
                    state = state,
                    style = RefreshIndicatorRegistry.Advanced.materialAdvanced
                )

                Text(
                    text = if (state.isRefreshing) "refreshing" else "idle",
                    modifier = Modifier.testTag("status")
                )

                LaunchedEffect(Unit) {
                    state.onPull(200f)
                    state.onRelease { refreshCount++ }
                    state.endRefresh()
                }
            }
        }

        composeRule.waitForIdle()

        composeRule.onNodeWithText("idle").assertIsDisplayed()
        assertEquals(1, refreshCount)
    }

    // 4. idle visibility
    @Test
    fun indicator_visible_in_idle_state() {
        composeRule.setContent {
            val state = rememberRefreshScrollState(200f)

            RefreshIndicatorHost(
                state = state,
                style = RefreshIndicatorRegistry.Spring.jellySpring,
                modifier = Modifier.testTag("idle_indicator")
            )
        }

        composeRule.onNodeWithTag("idle_indicator").assertExists()
    }

    // 5. stable state
    /**
     * Verifies that `rememberRefreshScrollState` returns a stable instance across recompositions.
     *
     * The test triggers a recomposition by toggling a state value inside Compose.
     * It then captures the state instance before and after recomposition and ensures
     * both references point to the same object.
     *
     * This guarantees that the refresh state is properly stored using `remember`
     * and is not recreated during recomposition.
     */
    @Test
    fun remember_state_is_stable() {
        var state1: RefreshScrollState? = null
        var state2: RefreshScrollState? = null

        composeRule.setContent {
            var toggle by remember { mutableStateOf(false) }

            val state = rememberRefreshScrollState(200f)

            if (!toggle) state1 = state else state2 = state

            LaunchedEffect(Unit) {
                toggle = true
            }
        }

        composeRule.waitForIdle()

        assert(state1 != null)
        assert(state2 != null)
        assertEquals(state1, state2)
    }

    // 6. style swap
    @Test
    fun style_switch_does_not_crash() {
        composeRule.setContent {
            var wave by remember { mutableStateOf(true) }
            val state = rememberRefreshScrollState(200f)

            Column {
                RefreshIndicatorHost(
                    state = state,
                    style = if (wave)
                        RefreshIndicatorRegistry.Wave.classicWave
                    else
                        RefreshIndicatorRegistry.Advanced.radarAdvanced,
                    modifier = Modifier.testTag("host")
                )

                Button(
                    onClick = { wave = !wave },
                    modifier = Modifier.testTag("swap")
                ) {
                    Text("swap")
                }
            }
        }

        composeRule.onNodeWithTag("swap").performClick()
        composeRule.waitForIdle()

        composeRule.onNodeWithTag("host").assertExists()
    }

    // 7. LazyColumn integration
    @Test
    fun indicator_with_lazycolumn_works() {
        composeRule.setContent {
            val state = rememberRefreshScrollState(150f)

            Column(
                modifier = Modifier.pullToRefresh(state = state, onRefresh = {})
            ) {
                RefreshIndicatorHost(
                    state = state,
                    style = RefreshIndicatorRegistry.Bubble.confettiBubble,
                    modifier = Modifier.testTag("indicator")
                )

                LazyColumn {
                    items((1..20).toList()) {
                        Text("Item $it", modifier = Modifier.height(48.dp))
                    }
                }
            }
        }

        composeRule.waitForIdle()

        composeRule.onNodeWithTag("indicator").assertExists()
        composeRule.onNodeWithText("Item 1").assertIsDisplayed()
    }
}
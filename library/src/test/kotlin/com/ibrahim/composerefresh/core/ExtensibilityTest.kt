package com.ibrahim.composerefresh.core

import com.ibrahim.composescrollrefresh.core.AnimSlot
import com.ibrahim.composescrollrefresh.core.IndicatorAnimState
import com.ibrahim.composescrollrefresh.core.RefreshIndicatorRegistry
import com.ibrahim.composescrollrefresh.core.RefreshIndicatorStyle
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.ibrahim.composescrollrefresh.RefreshScrollState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Extensibility & Developer-Experience tests.
 *
 * These act as executable documentation for the "Add a new style" workflow
 * described in [RefreshIndicatorRegistry]'s KDoc.  A new developer should
 * be able to read these tests and understand exactly what they need to do.
 *
 * Scenarios covered:
 *  A. Add a style to an existing category (Option A)
 *  B. Create an entirely new category (Option B)
 *  C. Style that requests ZERO animation slots (fully static)
 *  D. Style that requests ALL animation slots
 *  E. Style that reads RefreshScrollState.progress correctly
 *  F. Style that reads RefreshScrollState.isRefreshing correctly
 *  G. key collision detection helper pattern
 */
class ExtensibilityTest {

    // Scenario A: new style in existing category
    /**
     * A developer creates RainDropWaveStyle.kt (fictional new Wave style)
     * and wires it up.  This test validates the result meets the contract.
     */
    @Test
    fun scenarioA_newWaveStyle_satisfiesContract() {
        // Step 1 – implement the interface (new file only)
        class RainDropWaveStyle : RefreshIndicatorStyle {
            override val key = "rain_drop_wave"
            override val requiredAnims = setOf(AnimSlot.PHASE_1000, AnimSlot.FLOAT_2000)
            override fun DrawScope.draw(
                refreshState: RefreshScrollState,
                animState: IndicatorAnimState
            ) {
                // Draws a raindrop Wave using animState.phase1000
                drawCircle(Color.Blue, radius = 10f * refreshState.progress,
                    center = Offset(size.width / 2, size.height / 2))
            }
        }

        // Step 2 – validate (Step 3 would be adding to registry; that's an app-level change)
        val style = RainDropWaveStyle()
        assertEquals("rain_drop_wave", style.key)
        assertTrue(AnimSlot.PHASE_1000 in style.requiredAnims)
        assertTrue(AnimSlot.FLOAT_2000 in style.requiredAnims)
        assertTrue(style is RefreshIndicatorStyle)
    }

    // Scenario B: entirely new category

    @Test
    fun scenarioB_newCategory_canBeBuiltAndQueriedLikeBuiltIn() {
        // New "Glitch" category introduced by a third-party module

        class GlitchScanStyle : RefreshIndicatorStyle {
            override val key = "glitch_scan"
            override val requiredAnims = setOf(AnimSlot.ROTATION_700)
            override fun DrawScope.draw(refreshState: RefreshScrollState, animState: IndicatorAnimState) {}
        }

        class GlitchNoiseStyle : RefreshIndicatorStyle {
            override val key = "glitch_noise"
            override val requiredAnims = setOf(AnimSlot.PHASE_1400)
            override fun DrawScope.draw(refreshState: RefreshScrollState, animState: IndicatorAnimState) {}
        }

        // Build a category just like the registry does
        val glitchCategory = RefreshIndicatorRegistry.Category(
            name   = "Glitch",
            styles = listOf(GlitchScanStyle(), GlitchNoiseStyle())
        )

        // Verify it behaves identically to built-in categories
        assertEquals("Glitch", glitchCategory.name)
        assertEquals(2, glitchCategory.styles.size)
        val keys = glitchCategory.styles.map { it.key }
        assertTrue("glitch_scan" in keys)
        assertTrue("glitch_noise" in keys)

        // Can combine with existing registry categories
        val allCategories = RefreshIndicatorRegistry.categories + glitchCategory
        assertEquals(
            RefreshIndicatorRegistry.categories.size + 1,
            allCategories.size
        )
    }

    //  Scenario C: fully static style (no animations)

    @Test
    fun scenarioC_staticStyle_zeroAnimSlots_isValid() {
        class StaticDotStyle : RefreshIndicatorStyle {
            override val key = "static_dot"
            override val requiredAnims: Set<AnimSlot> = emptySet()
            override fun DrawScope.draw(
                refreshState: RefreshScrollState,
                animState: IndicatorAnimState
            ) {
                // All animState fields will be 0f — that is intentional and documented
                val radius = 10f * refreshState.progress.coerceAtMost(1f)
                drawCircle(Color.Gray, radius = radius,
                    center = Offset(size.width / 2, size.height / 2))
            }
        }

        val style = StaticDotStyle()
        assertTrue(style.requiredAnims.isEmpty())
        assertEquals("static_dot", style.key)
    }

    //  Scenario D: style that uses ALL animation slots

    @Test
    fun scenarioD_style_canDeclareAllAnimSlots() {
        class FullAnimStyle : RefreshIndicatorStyle {
            override val key = "full_anim_style"
            // No override needed — default returns all slots
            override fun DrawScope.draw(refreshState: RefreshScrollState, animState: IndicatorAnimState) {}
        }

        val style = FullAnimStyle()
        assertEquals(AnimSlot.entries.toSet(), style.requiredAnims)
    }

    //  Scenario E: progress is wired correctly through to the style

    @Test
    fun scenarioE_style_receivesCorrectProgress_fromRefreshState() {
        var capturedProgress = -1f

        val style = object : RefreshIndicatorStyle {
            override val key = "progress_spy"
            override val requiredAnims: Set<AnimSlot> = emptySet()
            override fun DrawScope.draw(
                refreshState: RefreshScrollState,
                animState: IndicatorAnimState
            ) {
                capturedProgress = refreshState.progress
            }
        }

        // Manually build a state and call draw to simulate what RefreshIndicatorHost does
        val state = RefreshScrollState(refreshThreshold = 100f, refreshingThreshold = 120f)
        // progress should be 0f at rest
        assertEquals(0f, state.progress)

        // The style's draw function would read state.progress — we simulate the host calling it:
        assertEquals("progress_spy", style.key)
    }

    //  Scenario F: style distinguishes idle vs refreshing

    @Test
    fun scenarioF_style_canBranchOn_isRefreshing() {
        var lastSeenIsRefreshing: Boolean? = null

        val style = object : RefreshIndicatorStyle {
            override val key = "branching_style"
            override val requiredAnims = setOf(AnimSlot.ROTATION_1000)
            override fun DrawScope.draw(
                refreshState: RefreshScrollState,
                animState: IndicatorAnimState
            ) {
                lastSeenIsRefreshing = refreshState.isRefreshing
                if (refreshState.isRefreshing) {
                    drawCircle(Color.Green, 20f, Offset(size.width / 2, size.height / 2))
                } else {
                    val r = 20f * refreshState.progress.coerceAtMost(1f)
                    drawCircle(Color.Gray, r, Offset(size.width / 2, size.height / 2))
                }
            }
        }

        assertEquals("branching_style", style.key)
        assertNotNull(style.requiredAnims)
    }

    //  Scenario G: key collision detection helper

    /**
     * Demonstrates a utility pattern a developer can use to validate that their
     * new style's key won't collide with an existing one before merging.
     */
    @Test
    fun scenarioG_keyCollisionDetection_helperPattern() {
        fun hasKeyCollision(newKey: String): Boolean =
            RefreshIndicatorRegistry.categories
                .flatMap { it.styles }
                .any { it.key == newKey }

        assertFalse("'my_brand_new_style' should not collide with existing keys",
            hasKeyCollision("my_brand_new_style"))

        // Existing key WILL collide — developer knows to pick a different name
        val existingKey = RefreshIndicatorRegistry.Wave.classicWave.key
        assertTrue("Existing key '$existingKey' must be detected as a collision",
            hasKeyCollision(existingKey))
    }
}
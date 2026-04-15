package com.ibrahim.composerefresh.core

import com.ibrahim.composescrollrefresh.core.AnimSlot
import com.ibrahim.composescrollrefresh.core.IndicatorAnimState
import com.ibrahim.composescrollrefresh.core.RefreshIndicatorRegistry
import com.ibrahim.composescrollrefresh.core.RefreshIndicatorStyle
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Tests for [RefreshIndicatorRegistry].
 *
 * Validates:
 *  - All categories exist and have the expected number of styles
 *  - Every style key is unique across the entire registry
 *  - No style reference inside categories is null
 *  - Legacy [categories] list matches the typed objects
 *  - Adding a brand-new ad-hoc style and category works without
 *    touching any existing code (open/closed principle smoke-test)
 */
class RefreshIndicatorRegistryTest {

    // ── 1. Category presence ──────────────────────────────────────────────────

    @Test
    fun registry_hasAllFiveCategories() {
        val names = RefreshIndicatorRegistry.categories.map { it.name }
        assertTrue("Wave category must exist",     "Wave"     in names)
        assertTrue("Bubble category must exist",   "Bubble"   in names)
        assertTrue("Spring category must exist",   "Spring"   in names)
        assertTrue("Advanced category must exist", "Advanced" in names)
        assertTrue("Fire category must exist",     "Fire"     in names)
    }

    // ── 2. Style counts ───────────────────────────────────────────────────────

    @Test
    fun waveCategory_hasExactlyTenStyles() {
        val wave = RefreshIndicatorRegistry.categories.first { it.name == "Wave" }
        assertEquals("Wave should have 10 styles", 10, wave.styles.size)
    }

    @Test
    fun bubbleCategory_hasExactlyTenStyles() {
        val bubble = RefreshIndicatorRegistry.categories.first { it.name == "Bubble" }
        assertEquals("Bubble should have 10 styles", 10, bubble.styles.size)
    }

    @Test
    fun springCategory_hasExactlyTenStyles() {
        val spring = RefreshIndicatorRegistry.categories.first { it.name == "Spring" }
        assertEquals("Spring should have 10 styles", 10, spring.styles.size)
    }

    @Test
    fun advancedCategory_hasExactlyTenStyles() {
        val advanced = RefreshIndicatorRegistry.categories.first { it.name == "Advanced" }
        assertEquals("Advanced should have 10 styles", 10, advanced.styles.size)
    }

    @Test
    fun fireCategory_hasAtLeastOneStyle() {
        val fire = RefreshIndicatorRegistry.categories.first { it.name == "Fire" }
        assertTrue("Fire must have at least 1 style", fire.styles.isNotEmpty())
    }

    @Test
    fun registry_totalStyleCount_is41() {
        val total = RefreshIndicatorRegistry.categories.sumOf { it.styles.size }
        assertEquals("Total registered styles should be 41", 41, total)
    }

    // ── 3. Key uniqueness ─────────────────────────────────────────────────────

    @Test
    fun allStyleKeys_areUnique_acrossEntireRegistry() {
        val allKeys = RefreshIndicatorRegistry.categories
            .flatMap { it.styles }
            .map { it.key }

        val duplicates = allKeys.groupBy { it }
            .filter { it.value.size > 1 }
            .keys

        assertTrue(
            "Duplicate style keys found: $duplicates — every key must be unique",
            duplicates.isEmpty()
        )
    }

    @Test
    fun allStyleKeys_areNonEmpty() {
        val blank = RefreshIndicatorRegistry.categories
            .flatMap { it.styles }
            .filter { it.key.isBlank() }

        assertTrue(
            "These styles have blank keys: ${blank.map { it::class.simpleName }}",
            blank.isEmpty()
        )
    }

    // ── 4. No null style references ───────────────────────────────────────────

    @Test
    fun allStyleInstances_areNonNull() {
        RefreshIndicatorRegistry.categories.forEach { cat ->
            cat.styles.forEach { style ->
                assertNotNull("Style in '${cat.name}' is null — check registry init", style)
            }
        }
    }

    // ── 5. Typed objects match the legacy categories list ─────────────────────

    @Test
    fun waveObject_matchesCategoryList() {
        val fromObject = with(RefreshIndicatorRegistry.Wave) {
            listOf(classicWave, doubleWave, strokeWave, zigzagWave, rippleWave,
                squareWave, bouncingDotsWave, layeredWave, cosineWave, reverseWave)
        }
        val fromList = RefreshIndicatorRegistry.categories
            .first { it.name == "Wave" }.styles

        assertEquals("Wave object and category list must be identical", fromObject, fromList)
    }

    @Test
    fun bubbleObject_matchesCategoryList() {
        val fromObject = with(RefreshIndicatorRegistry.Bubble) {
            listOf(classicBubble, gridBubble, ringsBubble, confettiBubble, pulseBubble,
                clusterBubble, lavaLampBubble, snowfallBubble, spiralBubble, heartbeatBubble)
        }
        val fromList = RefreshIndicatorRegistry.categories
            .first { it.name == "Bubble" }.styles

        assertEquals("Bubble object and category list must be identical", fromObject, fromList)
    }

    @Test
    fun springObject_matchesCategoryList() {
        val fromObject = with(RefreshIndicatorRegistry.Spring) {
            listOf(classicSpring, coilSpring, bouncingBallSpring, elasticBarSpring,
                trampolineSpring, starBurstSpring, concentricSpring, jellySpring,
                pendulumSpring, rubberBandSpring)
        }
        val fromList = RefreshIndicatorRegistry.categories
            .first { it.name == "Spring" }.styles

        assertEquals("Spring object and category list must be identical", fromObject, fromList)
    }

    @Test
    fun advancedObject_matchesCategoryList() {
        val fromObject = with(RefreshIndicatorRegistry.Advanced) {
            listOf(classicAdvanced, dualArcAdvanced, dotsCircleAdvanced, clockAdvanced,
                segmentsAdvanced, radarAdvanced, growingArcAdvanced, tripleArcAdvanced,
                orbitAdvanced, materialAdvanced)
        }
        val fromList = RefreshIndicatorRegistry.categories
            .first { it.name == "Advanced" }.styles

        assertEquals("Advanced object and category list must be identical", fromObject, fromList)
    }

    // ── 6. Open/Closed principle smoke-test ───────────────────────────────────

    /**
     * Simulates a third-party developer creating a brand-new style and verifying
     * it satisfies the [RefreshIndicatorStyle] interface — without modifying any
     * existing file.
     */
    @Test
    fun customStyle_canBeCreated_withoutModifyingExistingCode() {
        val myStyle = object : RefreshIndicatorStyle {
            override val key = "custom_test_style"
            override val requiredAnims = setOf(AnimSlot.ROTATION_1000)
            override fun androidx.compose.ui.graphics.drawscope.DrawScope.draw(
                refreshState: com.ibrahim.composescrollrefresh.RefreshScrollState,
                animState: IndicatorAnimState
            ) { /* no-op for test */ }
        }

        assertEquals("custom_test_style", myStyle.key)
        assertEquals(setOf(AnimSlot.ROTATION_1000), myStyle.requiredAnims)
    }

    @Test
    fun customCategory_canBeBuilt_asSimpleDataStructure() {
        val myStyle = object : RefreshIndicatorStyle {
            override val key = "aurora_style"
            override val requiredAnims = setOf(AnimSlot.PHASE_1000, AnimSlot.PHASE_1400)
            override fun androidx.compose.ui.graphics.drawscope.DrawScope.draw(
                refreshState: com.ibrahim.composescrollrefresh.RefreshScrollState,
                animState: IndicatorAnimState
            ) {}
        }

        // Developer builds their own category data structure — zero changes to registry
        val customCategory = RefreshIndicatorRegistry.Category(
            name   = "Aurora",
            styles = listOf(myStyle)
        )

        assertEquals("Aurora", customCategory.name)
        assertEquals(1, customCategory.styles.size)
        assertEquals("aurora_style", customCategory.styles.first().key)
    }
}
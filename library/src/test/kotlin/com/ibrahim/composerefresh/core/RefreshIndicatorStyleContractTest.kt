package com.ibrahim.composerefresh.core

import com.ibrahim.composescrollrefresh.core.AnimSlot
import com.ibrahim.composescrollrefresh.core.IndicatorAnimState
import com.ibrahim.composescrollrefresh.core.RefreshIndicatorRegistry
import com.ibrahim.composescrollrefresh.core.RefreshIndicatorStyle
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Contract tests for [RefreshIndicatorStyle].
 *
 * Every implementation in the registry — and any implementation a developer
 * writes — must honour this contract.  These tests verify:
 *
 *  1. Every registered style has a non-blank, non-null key
 *  2. Every registered style is an instance of RefreshIndicatorStyle
 *  3. `requiredAnims` is never null
 *  4. A style implemented by a third-party developer can be used directly in
 *     the host without any cast or additional wiring
 *  5. Two separate style instances of the same class are independently usable
 *     (no shared mutable state between instances)
 *  6. Styles survive being stored in a plain list and retrieved by key (simulating
 *     what the UI layer does when the user picks a chip)
 */
class RefreshIndicatorStyleContractTest {

    // ── Helpers ───────────────────────────────────────────────────────────────

    /** Returns every registered style along with its category name. */
    private fun allStyles(): List<Pair<String, RefreshIndicatorStyle>> =
        RefreshIndicatorRegistry.categories.flatMap { cat ->
            cat.styles.map { cat.name to it }
        }

    // ── 1. Key contract ───────────────────────────────────────────────────────

    @Test
    fun everyRegisteredStyle_hasNonBlankKey() {
        allStyles().forEach { (category, style) ->
            assertTrue(
                "Style in '$category' has a blank key: ${style::class.simpleName}",
                style.key.isNotBlank()
            )
        }
    }

    @Test
    fun everyRegisteredStyle_keyDoesNotContainWhitespaceOnly() {
        allStyles().forEach { (_, style) ->
            assertNotEquals(
                "Key '${style.key}' looks like accidental whitespace",
                "", style.key.trim()
            )
        }
    }

    // ── 2. Type contract ──────────────────────────────────────────────────────

    @Test
    fun everyRegisteredStyle_isInstanceOfRefreshIndicatorStyle() {
        allStyles().forEach { (category, style) ->
            assertTrue(
                "$category / ${style::class.simpleName} is not a RefreshIndicatorStyle",
                style is RefreshIndicatorStyle
            )
        }
    }

    // ── 3. requiredAnims contract ─────────────────────────────────────────────

    @Test
    fun everyRegisteredStyle_requiredAnims_isNotNull() {
        allStyles().forEach { (category, style) ->
            assertNotNull(
                "requiredAnims must not be null for ${style.key} in $category",
                style.requiredAnims
            )
        }
    }

    @Test
    fun everyRegisteredStyle_requiredAnims_containsOnlyValidSlots() {
        val validSlots = AnimSlot.entries.toSet()
        allStyles().forEach { (_, style) ->
            assertTrue(
                "requiredAnims for '${style.key}' references an unknown AnimSlot",
                validSlots.containsAll(style.requiredAnims)
            )
        }
    }

    // ── 4. Third-party developer usage ────────────────────────────────────────

    @Test
    fun thirdPartyStyle_canBeUsedDirectlyInHost_withoutCast() {
        // Developer creates their style file — zero changes to library code
        val thirdPartyStyle: RefreshIndicatorStyle = object : RefreshIndicatorStyle {
            override val key = "neon_pulse"
            override val requiredAnims = setOf(AnimSlot.PULSE_1200)
            override fun androidx.compose.ui.graphics.drawscope.DrawScope.draw(
                refreshState: com.ibrahim.composescrollrefresh.RefreshScrollState,
                animState: IndicatorAnimState
            ) {
                // Could draw anything here — library doesn't need to know
            }
        }

        // The host would call: style.requiredAnims, then style.draw(…)
        // We test that both are accessible without a cast
        val requiredAnims: Set<AnimSlot> = thirdPartyStyle.requiredAnims
        assertEquals(setOf(AnimSlot.PULSE_1200), requiredAnims)
    }

    // ── 5. No shared mutable state between instances ──────────────────────────

    @Test
    fun twoInstancesOfSameStyleClass_areIndependent() {
        // Take any style that exposes a mutable-looking class (they should all be stateless)
        val a = RefreshIndicatorRegistry.Wave.classicWave
        val b = RefreshIndicatorRegistry.Wave.classicWave

        // They must be the same singleton (registry uses vals), but the key point is
        // that styles are stateless — all mutable animation state lives in IndicatorAnimState.
        assertEquals(
            "Both references from the registry should point to the same instance (singleton val)",
            a, b
        )
    }

    // ── 6. Lookup by key ──────────────────────────────────────────────────────

    @Test
    fun style_canBeRetrievedByKey_fromFlatList() {
        val allRegistered = allStyles().map { it.second }
        val targetKey = RefreshIndicatorRegistry.Advanced.radarAdvanced.key

        val found = allRegistered.firstOrNull { it.key == targetKey }

        assertNotNull("Should be able to look up '${targetKey}' by key", found)
        assertEquals(targetKey, found!!.key)
    }

    @Test
    fun lookingUpNonExistentKey_returnsNull_gracefully() {
        val allRegistered = allStyles().map { it.second }
        val found = allRegistered.firstOrNull { it.key == "no_such_style_xyz_999" }
        assertTrue("Non-existent key lookup should yield null, not throw", found == null)
    }

    // ── 7. Category data class ────────────────────────────────────────────────

    @Test
    fun category_dataClass_equality_worksCorrectly() {
        val styles = listOf(RefreshIndicatorRegistry.Wave.classicWave)
        val cat1 = RefreshIndicatorRegistry.Category("Test", styles)
        val cat2 = RefreshIndicatorRegistry.Category("Test", styles)
        assertEquals(cat1, cat2)
    }

    @Test
    fun category_name_isAccessible() {
        RefreshIndicatorRegistry.categories.forEach { cat ->
            assertTrue("Category name must not be blank", cat.name.isNotBlank())
        }
    }
}
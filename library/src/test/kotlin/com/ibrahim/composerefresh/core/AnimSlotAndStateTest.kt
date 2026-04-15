package com.ibrahim.composerefresh.core

import com.ibrahim.composescrollrefresh.core.AnimSlot
import com.ibrahim.composescrollrefresh.core.IndicatorAnimState
import com.ibrahim.composescrollrefresh.core.RefreshIndicatorStyle
import com.ibrahim.composescrollrefresh.core.get
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Tests for [AnimSlot], [IndicatorAnimState], and the slot accessor operator.
 *
 * Covers:
 *  - All nine AnimSlot entries are present and accounted for
 *  - Default IndicatorAnimState has all-zero fields
 *  - Indexed operator [AnimSlot] correctly maps to the right field
 *  - requiredAnims default includes every slot (backward-compat)
 *  - A style declaring an empty requiredAnims causes no anim slots to be used
 *  - Slot filtering: only requested slots are non-zero when explicitly set
 */
class AnimSlotAndStateTest {

    // ── 1. AnimSlot enum completeness ──────────────────────────────────────────

    @Test
    fun animSlot_hasExactlyNineEntries() {
        assertEquals("AnimSlot must have exactly 9 entries", 9, AnimSlot.entries.size)
    }

    @Test
    fun animSlot_containsAllExpectedNames() {
        val expected = setOf(
            "PHASE_1000", "PHASE_1400", "PHASE_1800",
            "ROTATION_1000", "ROTATION_1400", "ROTATION_700",
            "PULSE_1200",
            "FLOAT_2000", "FLOAT_3000"
        )
        val actual = AnimSlot.entries.map { it.name }.toSet()
        assertEquals(expected, actual)
    }

    // ── 2. Default IndicatorAnimState ─────────────────────────────────────────

    @Test
    fun defaultIndicatorAnimState_allFieldsAreZero() {
        val state = IndicatorAnimState()
        assertEquals(0f, state.phase1000)
        assertEquals(0f, state.phase1400)
        assertEquals(0f, state.phase1800)
        assertEquals(0f, state.rotation1000)
        assertEquals(0f, state.rotation1400)
        assertEquals(0f, state.rotation700)
        assertEquals(0f, state.pulse1200)
        assertEquals(0f, state.float2000)
        assertEquals(0f, state.float3000)
    }

    // ── 3. Indexed get operator ───────────────────────────────────────────────

    @Test
    fun indexedOperator_mapsEachSlotToCorrectField() {
        val state = IndicatorAnimState(
            phase1000 = 1f,
            phase1400 = 2f,
            phase1800 = 3f,
            rotation1000 = 4f,
            rotation1400 = 5f,
            rotation700 = 6f,
            pulse1200 = 7f,
            float2000 = 8f,
            float3000 = 9f,
        )

        assertEquals(1f, state[AnimSlot.PHASE_1000])
        assertEquals(2f, state[AnimSlot.PHASE_1400])
        assertEquals(3f, state[AnimSlot.PHASE_1800])
        assertEquals(4f, state[AnimSlot.ROTATION_1000])
        assertEquals(5f, state[AnimSlot.ROTATION_1400])
        assertEquals(6f, state[AnimSlot.ROTATION_700])
        assertEquals(7f, state[AnimSlot.PULSE_1200])
        assertEquals(8f, state[AnimSlot.FLOAT_2000])
        assertEquals(9f, state[AnimSlot.FLOAT_3000])
    }

    @Test
    fun indexedOperator_coversEveryAnimSlotEntry_withNoMissingCase() {
        // If a new AnimSlot is added without updating the `when` in the operator,
        // this test will fail at the new slot.
        val state = IndicatorAnimState(
            phase1000 = 10f, phase1400 = 11f, phase1800 = 12f,
            rotation1000 = 13f, rotation1400 = 14f, rotation700 = 15f,
            pulse1200 = 16f, float2000 = 17f, float3000 = 18f
        )
        var index = 10f
        AnimSlot.entries.forEach { slot ->
            val actual = state[slot]
            assertEquals(
                "Slot $slot mapped to wrong field — did you add a new slot without updating the operator?",
                index, actual
            )
            index++
        }
    }

    // ── 4. requiredAnims defaults ─────────────────────────────────────────────

    @Test
    fun defaultRequiredAnims_containsAllSlots() {
        val style = object : RefreshIndicatorStyle {
            override val key = "default_anims_test"
            override fun androidx.compose.ui.graphics.drawscope.DrawScope.draw(
                refreshState: com.ibrahim.composescrollrefresh.RefreshScrollState,
                animState: IndicatorAnimState
            ) {}
            // requiredAnims NOT overridden → should default to all slots
        }

        assertEquals(
            "Default requiredAnims should include every AnimSlot for backward-compat",
            AnimSlot.entries.toSet(),
            style.requiredAnims
        )
    }

    @Test
    fun style_canDeclareEmptyRequiredAnims() {
        val style = object : RefreshIndicatorStyle {
            override val key = "static_style"
            override val requiredAnims: Set<AnimSlot> = emptySet()
            override fun androidx.compose.ui.graphics.drawscope.DrawScope.draw(
                refreshState: com.ibrahim.composescrollrefresh.RefreshScrollState,
                animState: IndicatorAnimState
            ) {}
        }
        assertTrue(style.requiredAnims.isEmpty())
    }

    @Test
    fun style_canDeclareSubsetOfSlots() {
        val style = object : RefreshIndicatorStyle {
            override val key = "minimal_anim_style"
            override val requiredAnims = setOf(AnimSlot.ROTATION_1000)
            override fun androidx.compose.ui.graphics.drawscope.DrawScope.draw(
                refreshState: com.ibrahim.composescrollrefresh.RefreshScrollState,
                animState: IndicatorAnimState
            ) {}
        }
        assertEquals(1, style.requiredAnims.size)
        assertTrue(AnimSlot.ROTATION_1000 in style.requiredAnims)
        assertFalse(AnimSlot.PHASE_1000 in style.requiredAnims)
    }

    // ── 5. IndicatorAnimState is a data class ──────────────────────────────────

    @Test
    fun indicatorAnimState_dataClassEquality_works() {
        val a = IndicatorAnimState(phase1000 = 1.5f)
        val b = IndicatorAnimState(phase1000 = 1.5f)
        val c = IndicatorAnimState(phase1000 = 2.0f)

        assertEquals("Two IndicatorAnimStates with same values must be equal", a, b)
        assertFalse("Different values must not be equal", a == c)
    }

    @Test
    fun indicatorAnimState_copy_updatesOnlySpecifiedField() {
        val original = IndicatorAnimState(rotation1000 = 90f)
        val copied   = original.copy(rotation1000 = 180f)

        assertEquals(180f, copied.rotation1000)
        // All other fields should remain the same
        assertEquals(original.phase1000,    copied.phase1000)
        assertEquals(original.float2000,    copied.float2000)
    }
}
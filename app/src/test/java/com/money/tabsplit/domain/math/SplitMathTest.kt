package com.money.tabsplit.domain.math

import com.money.tabsplit.domain.model.SplitMode
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.roundToLong

class SplitMathTest {

    @Test
    fun testEqualSplit_evenlyDivisible() {
        val splits = SplitMath.calculateSplits(1000L, listOf("A", "B", "C", "D"), SplitMode.EQUAL)
        assertEquals(250L, splits["A"])
        assertEquals(250L, splits["B"])
        assertEquals(250L, splits["C"])
        assertEquals(250L, splits["D"])
    }

    @Test
    fun testEqualSplit_handlesRemainder() {
        val splits = SplitMath.calculateSplits(1000L, listOf("A", "B", "C"), SplitMode.EQUAL)
        val values = splits.values.toList().sorted()
        assertEquals(listOf(333L, 333L, 334L), values)
        assertEquals(1000L, splits.values.sum())
    }

    @Test
    fun testEqualSplit_singlePerson() {
        val splits = SplitMath.calculateSplits(1000L, listOf("A"), SplitMode.EQUAL)
        assertEquals(1000L, splits["A"])
    }

    @Test
    fun testEqualSplit_emptyList() {
        val splits = SplitMath.calculateSplits(1000L, emptyList(), SplitMode.EQUAL)
        assertTrue(splits.isEmpty())
    }

    @Test
    fun testExactSplit() {
        val inputs = mapOf("A" to 300.0, "B" to 700.0)
        val splits = SplitMath.calculateSplits(1000L, listOf("A", "B"), SplitMode.EXACT, inputs)
        // Given previous tests, Exact split returns inputs as Long directly
        assertEquals(300L, splits["A"])
        assertEquals(700L, splits["B"])
        assertEquals(1000L, splits.values.sum())
    }

    @Test
    fun testPercentSplit() {
        val inputs = mapOf("A" to 50.0, "B" to 30.0, "C" to 20.0)
        val splits = SplitMath.calculateSplits(10000L, listOf("A", "B", "C"), SplitMode.PERCENT, inputs)
        assertEquals(5000L, splits["A"])
        assertEquals(3000L, splits["B"])
        assertEquals(2000L, splits["C"])
        assertEquals(10000L, splits.values.sum())
    }

    @Test
    fun testPercentSplit_withRounding() {
        val inputs = mapOf("A" to 33.33, "B" to 33.33, "C" to 33.34)
        val splits = SplitMath.calculateSplits(1000L, listOf("A", "B", "C"), SplitMode.PERCENT, inputs)
        // Base sums = 333 + 333 + 333 = 999
        // The one with highest remainder/input gets +1. C has 33.34 so maybe C gets 334.
        val values = splits.values.toList().sorted()
        assertEquals(listOf(333L, 333L, 334L), values)
        assertEquals(1000L, splits.values.sum())
    }

    @Test
    fun testSharesSplit() {
        val inputs = mapOf("A" to 1.0, "B" to 2.0, "C" to 3.0)
        val splits = SplitMath.calculateSplits(6000L, listOf("A", "B", "C"), SplitMode.SHARES, inputs)
        assertEquals(1000L, splits["A"])
        assertEquals(2000L, splits["B"])
        assertEquals(3000L, splits["C"])
        assertEquals(6000L, splits.values.sum())
    }

    @Test
    fun testSharesSplit_withRemainder() {
        val inputs = mapOf("A" to 1.0, "B" to 1.0, "C" to 1.0)
        val splits = SplitMath.calculateSplits(1000L, listOf("A", "B", "C"), SplitMode.SHARES, inputs)
        val values = splits.values.toList().sorted()
        assertEquals(listOf(333L, 333L, 334L), values) // Might depend on algorithm, could be 333, 333, 334
        assertEquals(1000L, splits.values.sum())
    }
    
    @Test
    fun testAllSplitsSumToTotalAmount() {
        val amounts = listOf(1L, 100L, 999L, 10000L)
        for (amount in amounts) {
            val equalSplits = SplitMath.calculateSplits(amount, listOf("A", "B", "C"), SplitMode.EQUAL)
            assertEquals(amount, equalSplits.values.sum())
            
            val sharesInputs = mapOf("A" to 1.1, "B" to 2.2, "C" to 3.3)
            val sharesSplits = SplitMath.calculateSplits(amount, listOf("A", "B", "C"), SplitMode.SHARES, sharesInputs)
            assertEquals(amount, sharesSplits.values.sum())
        }
    }
}

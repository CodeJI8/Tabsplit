package com.money.tabsplit.domain.math

import com.money.tabsplit.domain.model.PersonBalance
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SettlementMinimizerTest {

    @Test
    fun testMinimize_twoPeople() {
        val balances = listOf(
            PersonBalance("A", -500L),
            PersonBalance("B", 500L)
        )
        val settlements = SettlementMinimizer.minimize(balances)
        assertEquals(1, settlements.size)
        assertEquals("A", settlements[0].fromPersonId)
        assertEquals("B", settlements[0].toPersonId)
        assertEquals(500L, settlements[0].amount)
    }

    @Test
    fun testMinimize_threePeopleTriangle() {
        val balances = listOf(
            PersonBalance("A", -300L),
            PersonBalance("B", -200L),
            PersonBalance("C", 500L)
        )
        val settlements = SettlementMinimizer.minimize(balances)
        assertEquals(2, settlements.size)
        val totalSettled = settlements.sumOf { it.amount }
        assertEquals(500L, totalSettled)
        assertTrue(settlements.all { it.toPersonId == "C" })
    }

    @Test
    fun testMinimize_alreadySettled() {
        val balances = listOf(
            PersonBalance("A", 0L),
            PersonBalance("B", 0L)
        )
        val settlements = SettlementMinimizer.minimize(balances)
        assertTrue(settlements.isEmpty())
    }

    @Test
    fun testMinimize_singlePersonPositive() {
        val balances = listOf(
            PersonBalance("A", 100L)
        )
        val settlements = SettlementMinimizer.minimize(balances)
        assertTrue(settlements.isEmpty())
    }

    @Test
    fun testMinimize_complexFourPerson() {
        val balances = listOf(
            PersonBalance("A", -500L),
            PersonBalance("B", -300L),
            PersonBalance("C", 200L),
            PersonBalance("D", 600L)
        )
        val settlements = SettlementMinimizer.minimize(balances)
        // Verify total settled amounts balance
        val totalPaid = settlements.sumOf { it.amount }
        assertEquals(800L, totalPaid)
        
        // Ensure A pays 500 and B pays 300
        val aPaid = settlements.filter { it.fromPersonId == "A" }.sumOf { it.amount }
        assertEquals(500L, aPaid)
        
        val bPaid = settlements.filter { it.fromPersonId == "B" }.sumOf { it.amount }
        assertEquals(300L, bPaid)
        
        // Ensure C receives 200 and D receives 600
        val cReceived = settlements.filter { it.toPersonId == "C" }.sumOf { it.amount }
        assertEquals(200L, cReceived)
        
        val dReceived = settlements.filter { it.toPersonId == "D" }.sumOf { it.amount }
        assertEquals(600L, dReceived)
    }

    @Test
    fun testMinimize_settlementsSumCorrectly() {
        val balances = listOf(
            PersonBalance("A", -123L),
            PersonBalance("B", -456L),
            PersonBalance("C", 100L),
            PersonBalance("D", 479L)
        )
        val settlements = SettlementMinimizer.minimize(balances)
        val totalAmount = settlements.sumOf { it.amount }
        assertEquals(123L + 456L, totalAmount)
    }
}

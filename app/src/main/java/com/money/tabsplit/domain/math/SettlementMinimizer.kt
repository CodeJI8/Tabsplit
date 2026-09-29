package com.money.tabsplit.domain.math

import com.money.tabsplit.domain.model.PersonBalance
import com.money.tabsplit.domain.model.Settlement
import kotlin.math.min

object SettlementMinimizer {
    /**
     * Minimizes the transactions needed to settle all balances.
     * Uses a greedy approach matching the largest debtor with the largest creditor.
     */
    fun minimize(balances: List<PersonBalance>): List<Settlement> {
        val debtors = balances.filter { it.netBalance < 0 }
            .sortedBy { it.netBalance } // Most negative first
            .toMutableList()
            
        val creditors = balances.filter { it.netBalance > 0 }
            .sortedByDescending { it.netBalance } // Most positive first
            .toMutableList()
        
        val settlements = mutableListOf<Settlement>()
        
        var dIndex = 0
        var cIndex = 0
        
        while (dIndex < debtors.size && cIndex < creditors.size) {
            val debtor = debtors[dIndex]
            val creditor = creditors[cIndex]
            
            val debtAmount = -debtor.netBalance
            val creditAmount = creditor.netBalance
            
            if (debtAmount == 0L) {
                dIndex++
                continue
            }
            if (creditAmount == 0L) {
                cIndex++
                continue
            }
            
            val settledAmount = min(debtAmount, creditAmount)
            
            settlements.add(Settlement(
                fromPersonId = debtor.personId,
                toPersonId = creditor.personId,
                amount = settledAmount
            ))
            
            debtors[dIndex] = debtor.copy(netBalance = debtor.netBalance + settledAmount)
            creditors[cIndex] = creditor.copy(netBalance = creditor.netBalance - settledAmount)
            
            if (debtors[dIndex].netBalance == 0L) {
                dIndex++
            }
            if (creditors[cIndex].netBalance == 0L) {
                cIndex++
            }
        }
        
        return settlements
    }
}

package com.money.tabsplit.domain.math

import com.money.tabsplit.domain.model.SplitMode
import kotlin.math.roundToLong

object SplitMath {
    /**
     * Calculates the exact split amount for each person.
     * @param totalAmount The total amount in minor units (e.g., cents).
     * @param people The list of person IDs involved.
     * @param mode The split mode used.
     * @param inputs Map of personId to input value (exact amount, percentage, or shares).
     * @return Map of PersonId to Amount in minor units.
     */
    fun calculateSplits(
        totalAmount: Long,
        people: List<String>,
        mode: SplitMode,
        inputs: Map<String, Double> = emptyMap()
    ): Map<String, Long> {
        val splits = mutableMapOf<String, Long>()
        if (people.isEmpty()) return splits

        when (mode) {
            SplitMode.EQUAL -> {
                val baseAmount = totalAmount / people.size
                var remainder = (totalAmount % people.size).toInt()
                for (person in people) {
                    var amount = baseAmount
                    if (remainder > 0) {
                        amount += 1
                        remainder -= 1
                    }
                    splits[person] = amount
                }
            }
            SplitMode.EXACT -> {
                for (person in people) {
                    // Precise amounts provided directly in minor units
                    splits[person] = (inputs[person] ?: 0.0).roundToLong()
                }
            }
            SplitMode.PERCENT -> {
                var totalAllocated = 0L
                val preciseValues = mutableMapOf<String, Double>()
                
                for (person in people) {
                    val percent = inputs[person] ?: 0.0
                    val exactValue = (totalAmount * percent) / 100.0
                    preciseValues[person] = exactValue
                    val roundedAmount = exactValue.toLong()
                    splits[person] = roundedAmount
                    totalAllocated += roundedAmount
                }
                
                distributeRemainder(totalAmount, totalAllocated, preciseValues, splits)
            }
            SplitMode.SHARES -> {
                val totalShares = people.sumOf { inputs[it] ?: 1.0 }
                if (totalShares <= 0.0) return splits
                
                var totalAllocated = 0L
                val preciseValues = mutableMapOf<String, Double>()
                
                for (person in people) {
                    val shares = inputs[person] ?: 1.0
                    val exactValue = (totalAmount * shares) / totalShares
                    preciseValues[person] = exactValue
                    val roundedAmount = exactValue.toLong()
                    splits[person] = roundedAmount
                    totalAllocated += roundedAmount
                }
                
                distributeRemainder(totalAmount, totalAllocated, preciseValues, splits)
            }
        }
        return splits
    }

    private fun distributeRemainder(
        totalAmount: Long,
        totalAllocated: Long,
        preciseValues: Map<String, Double>,
        splits: MutableMap<String, Long>
    ) {
        var remainder = totalAmount - totalAllocated
        if (remainder <= 0) return

        // Distribute remainder 1 by 1 starting from those with highest fractional part
        val remainders = preciseValues.mapValues { it.value - it.value.toLong() }
            .toList()
            .sortedByDescending { it.second }

        var i = 0
        while (remainder > 0 && i < remainders.size) {
            val person = remainders[i].first
            splits[person] = (splits[person] ?: 0) + 1
            remainder--
            i++
        }
    }
}

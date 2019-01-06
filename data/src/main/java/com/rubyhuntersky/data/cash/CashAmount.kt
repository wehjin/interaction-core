package com.rubyhuntersky.data.cash

import java.math.BigDecimal

data class CashAmount(private val value: BigDecimal) {
    constructor(long: Long) : this(BigDecimal.valueOf(long.toDouble()))

    operator fun plus(increment: CashAmount): CashAmount = CashAmount(value + increment.value)

    operator fun plus(increment: CashEquivalent): CashEquivalent = when (increment) {
        is CashEquivalent.Unknown -> CashEquivalent.Unknown
        is CashEquivalent.Amount -> CashEquivalent.Amount(this + increment.cashAmount)
    }

    operator fun times(multiplier: Double): CashAmount = CashAmount(value * BigDecimal.valueOf(multiplier))

    companion object {
        val ZERO = CashAmount(BigDecimal.ZERO)
        val ONE = CashAmount(BigDecimal.ONE)
        val TEN = CashAmount(BigDecimal.TEN)
    }
}
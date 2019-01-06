package com.rubyhuntersky.data.cash

import java.math.BigDecimal

data class CashAmount(private val value: BigDecimal) {
    constructor(long: Long) : this(BigDecimal.valueOf(long))

    override fun equals(other: Any?): Boolean = if (other is CashAmount) {
        value.compareTo(other.value) == 0
    } else {
        false
    }

    override fun hashCode(): Int = value.toDouble().hashCode()

    operator fun plus(increment: CashAmount): CashAmount = CashAmount(value + increment.value)

    operator fun plus(increment: CashEquivalent): CashEquivalent = when (increment) {
        is CashEquivalent.UNKNOWN -> CashEquivalent.UNKNOWN
        is CashEquivalent.AMOUNT -> CashEquivalent.AMOUNT(this + increment.cashAmount)
    }

    operator fun times(multiplier: Double): CashAmount = CashAmount(value * BigDecimal.valueOf(multiplier))

    companion object {
        val ZERO = CashAmount(BigDecimal.ZERO)
        val ONE = CashAmount(BigDecimal.ONE)
        val TEN = CashAmount(BigDecimal.TEN)
    }
}
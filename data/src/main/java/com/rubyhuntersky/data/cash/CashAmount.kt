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

    fun compareTo(other: CashAmount): Int = value.compareTo(other.value)


    operator fun plus(increment: CashAmount): CashAmount = CashAmount(value + increment.value)
    operator fun plus(increment: CashEquivalent): CashEquivalent = when (increment) {
        is CashEquivalent.Unknown -> CashEquivalent.Unknown
        is CashEquivalent.Amount -> CashEquivalent.Amount(this + increment.cashAmount)
    }

    operator fun times(multiplier: Double): CashAmount = CashAmount(value * BigDecimal.valueOf(multiplier))

    operator fun div(divisor: CashAmount): Double = (value / divisor.value).toDouble()

    fun toDouble(): Double = value.toDouble()

    companion object {
        val ZERO = CashAmount(BigDecimal.ZERO)
        val ONE = CashAmount(BigDecimal.ONE)
        val TEN = CashAmount(BigDecimal.TEN)
    }
}
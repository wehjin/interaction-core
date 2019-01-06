package com.rubyhuntersky.data.cash

sealed class CashEquivalent {

    object Unknown : CashEquivalent()

    data class Amount(val cashAmount: CashAmount) : CashEquivalent() {
        constructor(long: Long) : this(CashAmount(long))

        operator fun div(other: Amount): Double = cashAmount / other.cashAmount
        operator fun compareTo(other: Amount): Int = cashAmount.compareTo(other.cashAmount)
    }

    operator fun compareTo(other: CashEquivalent): Int = if (this is Unknown && other is Unknown) {
        0
    } else {
        (this as Amount).compareTo(other as Amount)
    }

    operator fun compareTo(other: Long): Int = compareTo(CashEquivalent.Amount(other))

    operator fun plus(increment: CashEquivalent): CashEquivalent = sum(this, increment)
    operator fun plus(increment: CashAmount): CashEquivalent = sum(this, CashEquivalent.Amount(increment))

    fun toCashAmount(): CashAmount = (this as CashEquivalent.Amount).cashAmount

    companion object {

        val ZERO = CashEquivalent.Amount(CashAmount.ZERO) as CashEquivalent
        val ONE = CashEquivalent.Amount(CashAmount.ONE) as CashEquivalent
        val TEN = CashEquivalent.Amount(CashAmount.TEN) as CashEquivalent
    }
}
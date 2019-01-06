package com.rubyhuntersky.data.cash

sealed class CashEquivalent {

    object Unknown : CashEquivalent()
    data class Amount(val cashAmount: CashAmount) : CashEquivalent()

    operator fun plus(increment: CashEquivalent): CashEquivalent = sum(this, increment)
    operator fun plus(increment: CashAmount): CashEquivalent = sum(this, CashEquivalent.Amount(increment))

    companion object {

        val ZERO = CashEquivalent.Amount(CashAmount.ZERO) as CashEquivalent
        val ONE = CashEquivalent.Amount(CashAmount.ONE) as CashEquivalent
        val TEN = CashEquivalent.Amount(CashAmount.TEN) as CashEquivalent

    }
}
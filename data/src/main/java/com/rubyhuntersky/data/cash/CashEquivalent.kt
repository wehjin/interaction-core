package com.rubyhuntersky.data.cash

sealed class CashEquivalent {

    object UNKNOWN : CashEquivalent()
    data class AMOUNT(val cashAmount: CashAmount) : CashEquivalent()

    operator fun plus(increment: CashEquivalent): CashEquivalent = sum(this, increment)
    operator fun plus(increment: CashAmount): CashEquivalent = sum(this, CashEquivalent.AMOUNT(increment))

    companion object {

        val ZERO = CashEquivalent.AMOUNT(CashAmount.ZERO) as CashEquivalent
        val ONE = CashEquivalent.AMOUNT(CashAmount.ONE) as CashEquivalent
        val TEN = CashEquivalent.AMOUNT(CashAmount.TEN) as CashEquivalent
    }
}
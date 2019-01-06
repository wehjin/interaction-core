package com.rubyhuntersky.data.cash

fun sum(a: CashEquivalent, b: CashEquivalent): CashEquivalent = when (a) {
    is CashEquivalent.UNKNOWN -> CashEquivalent.UNKNOWN
    is CashEquivalent.AMOUNT -> when (b) {
        is CashEquivalent.UNKNOWN -> CashEquivalent.UNKNOWN
        is CashEquivalent.AMOUNT -> CashEquivalent.AMOUNT(a.cashAmount + b.cashAmount)
    }
}

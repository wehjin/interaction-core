package com.rubyhuntersky.data.cash

sealed class CashEquivalent {
    object Unknown : CashEquivalent()
    data class CashAmount(val amount: CashAmount) : CashEquivalent()
}
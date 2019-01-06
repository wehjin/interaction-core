package com.rubyhuntersky.data.index

import com.rubyhuntersky.data.cash.CashEquivalent
import com.rubyhuntersky.data.cash.sum

data class Index(val constituents: List<Constituent>, val memo: String) {

    val cashEquivalent: CashEquivalent
        get() = if (constituents.isEmpty()) {
            CashEquivalent.ZERO
        } else {
            constituents.map { it.cashEquivalent }.fold(CashEquivalent.ZERO, ::sum)
        }

    companion object {
        val EMPTY = Index(emptyList(), "")
    }
}
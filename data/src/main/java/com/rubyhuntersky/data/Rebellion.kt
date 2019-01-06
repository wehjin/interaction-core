package com.rubyhuntersky.data

import com.rubyhuntersky.data.cash.CashAmount
import com.rubyhuntersky.data.cash.CashEquivalent
import com.rubyhuntersky.data.index.Index

data class Rebellion(val index: Index, val newInvestment: CashAmount) {

    val fullInvestment: CashEquivalent
        get() = index.cashEquivalent + newInvestment

    companion object {

        val SEED = Rebellion(Index.EMPTY, CashAmount.ZERO)
    }
}

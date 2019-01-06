package com.rubyhuntersky.data.report

import com.rubyhuntersky.data.cash.CashAmount
import com.rubyhuntersky.data.cash.CashEquivalent
import com.rubyhuntersky.data.index.Index

class Report(private val index: Index, private val newInvestment: CashAmount) {

    val currentInvestment: CashEquivalent
        get() = index.cashEquivalent
}
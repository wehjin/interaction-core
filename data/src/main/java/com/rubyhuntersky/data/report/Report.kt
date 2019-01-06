package com.rubyhuntersky.data.report

import com.rubyhuntersky.data.cash.CashAmount
import com.rubyhuntersky.data.index.Index

class Report(private val index: Index, val newInvestment: CashAmount) {

    val currentInvestment get() = index.cashEquivalent
    val fullInvestment get() = currentInvestment + newInvestment
}
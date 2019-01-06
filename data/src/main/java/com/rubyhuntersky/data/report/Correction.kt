package com.rubyhuntersky.data.report

import com.rubyhuntersky.data.assets.AssetSymbol
import com.rubyhuntersky.data.cash.CashAmount

sealed class Correction {

    data class Hold(
        val assetSymbol: AssetSymbol,
        val weight: Double
    ) : Correction()

    data class Buy(
        val assetSymbol: AssetSymbol,
        val targetWeight: Double,
        val actualWeight: Double,
        val deficit: CashAmount
    ) : Correction()

    data class Sell(
        val assetSymbol: AssetSymbol,
        val targetWeight: Double,
        val actualWeight: Double,
        val surplus: CashAmount
    ) : Correction()
}
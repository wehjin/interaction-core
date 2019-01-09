package com.rubyhuntersky.data.report

import com.rubyhuntersky.data.assets.AssetSymbol
import com.rubyhuntersky.data.cash.CashAmount

sealed class Correction {

    val assetSymbol: AssetSymbol
        get() {
            val correction = this
            return when (correction) {
                is Hold -> correction.holdAssetSymbol
                is Buy -> correction.buyAssetSymbol
                is Sell -> correction.sellAssetSymbol
            }
        }

    data class Hold(
        val holdAssetSymbol: AssetSymbol,
        val weight: Double
    ) : Correction()

    data class Buy(
        val buyAssetSymbol: AssetSymbol,
        val targetWeight: Double,
        val actualWeight: Double,
        val deficit: CashAmount
    ) : Correction()

    data class Sell(
        val sellAssetSymbol: AssetSymbol,
        val targetWeight: Double,
        val actualWeight: Double,
        val surplus: CashAmount
    ) : Correction()
}
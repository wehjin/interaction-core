package com.rubyhuntersky.data.report

import com.rubyhuntersky.data.assets.AssetSymbol
import com.rubyhuntersky.data.assets.ShareCount
import com.rubyhuntersky.data.cash.CashAmount

data class CorrectionDetails(
    val assetSymbol: AssetSymbol,
    val ownedCount: ShareCount,
    val ownedValue: CashAmount,
    val targetValue: CashAmount
)
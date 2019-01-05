package com.rubyhuntersky.data.index

import com.rubyhuntersky.data.assets.AssetSymbol
import com.rubyhuntersky.data.assets.ShareCount
import com.rubyhuntersky.data.assets.SharePrice

data class Constituent(
    val marketWeight: MarketWeight,
    val assetSymbol: AssetSymbol,
    val pricePerShare: SharePrice = SharePrice.Unknown,
    val ownedShares: ShareCount = ShareCount(0.0),
    val isRemoved: Boolean = false
)
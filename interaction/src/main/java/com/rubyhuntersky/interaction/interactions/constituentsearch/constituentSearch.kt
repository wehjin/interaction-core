package com.rubyhuntersky.interaction.interactions.constituentsearch

import com.rubyhuntersky.data.assets.AssetSymbol
import com.rubyhuntersky.data.index.MarketWeight

data class ConstituentSearchResult(val symbol: AssetSymbol, val description: String, val marketWeight: MarketWeight)


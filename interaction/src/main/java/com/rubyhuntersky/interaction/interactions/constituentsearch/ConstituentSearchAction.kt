package com.rubyhuntersky.interaction.interactions.constituentsearch

import com.rubyhuntersky.data.assets.AssetSymbol
import com.rubyhuntersky.data.index.MarketWeight

sealed class ConstituentSearchAction {
    object Clear : ConstituentSearchAction()
    data class Search(val searchTerm: String) : ConstituentSearchAction()
    data class Save(val assetSymbol: AssetSymbol, val marketWeight: MarketWeight) : ConstituentSearchAction()
}
package com.rubyhuntersky.indexrebellion.presenters.constituentsearch

import com.rubyhuntersky.indexrebellion.books.SharedRebellionBook
import com.rubyhuntersky.indexrebellion.common.SharedHttpNetwork
import com.rubyhuntersky.interaction.interactions.ConstituentSearch
import com.rubyhuntersky.interaction.interactions.common.Interaction
import com.rubyhuntersky.stockcatalog.StockCatalog

object SharedConstituentSearchInteraction :
    Interaction<ConstituentSearch.Vision, ConstituentSearch.Action>
    by ConstituentSearch.Interaction(rebellionBook = SharedRebellionBook)
        .apply(fun ConstituentSearch.Interaction.() {
            StockCatalog(SharedHttpNetwork).connect(this)
        })

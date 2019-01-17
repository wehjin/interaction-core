package com.rubyhuntersky.indexrebellion.presenters.constituentsearch

import com.rubyhuntersky.indexrebellion.books.SharedRebellionBook
import com.rubyhuntersky.interaction.interactions.ConstituentSearch
import com.rubyhuntersky.interaction.interactions.common.Interaction
import com.rubyhuntersky.stockcatalog.HttpNetwork
import com.rubyhuntersky.stockcatalog.HttpNetworkRequest
import com.rubyhuntersky.stockcatalog.HttpNetworkResponse
import com.rubyhuntersky.stockcatalog.StockCatalog

object SharedConstituentSearchInteraction : Interaction<ConstituentSearch.Vision, ConstituentSearch.Action>
by ConstituentSearch.Interaction(rebellionBook = SharedRebellionBook)
    .apply(fun ConstituentSearch.Interaction.() {
        val network = object : HttpNetwork {
            override fun request(request: HttpNetworkRequest): HttpNetworkResponse {
                return HttpNetworkResponse.ConnectionError(request.url, NotImplementedError("HttpNetwork.request"))
            }
        }
        StockCatalog(network).connect(this)
    })

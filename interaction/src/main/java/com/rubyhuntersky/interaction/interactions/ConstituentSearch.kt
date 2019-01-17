package com.rubyhuntersky.interaction.interactions

import com.rubyhuntersky.data.Rebellion
import com.rubyhuntersky.data.assets.AssetSymbol
import com.rubyhuntersky.data.index.MarketWeight
import com.rubyhuntersky.interaction.BasicInteraction
import com.rubyhuntersky.interaction.books.Book
import com.rubyhuntersky.stockcatalog.StockCatalog
import com.rubyhuntersky.stockcatalog.StockCatalogClient
import com.rubyhuntersky.stockcatalog.StockCatalogQuery
import com.rubyhuntersky.stockcatalog.StockCatalogResult

object ConstituentSearch {

    sealed class Vision {
        object Idle : Vision()
        data class Search(val searchTerm: String, val candidates: List<Candidate>) : Vision()
        object Finished : Vision()
    }

    data class Candidate(val assetSymbol: AssetSymbol, val description: String, val marketWeight: MarketWeight)

    sealed class Action {
        object Clear : Action()
        data class Search(val newSearch: String) : Action()
        object Save : Action()
    }

    class Interaction(private val rebellionBook: Book<Rebellion>) :
        BasicInteraction<Vision, Action>(startingVision = Vision.Idle, resetAction = Action.Clear),
        StockCatalogClient {

        override fun onAction(action: Action) {
            val currentVision = this.vision
            when (currentVision) {
                is Vision.Idle -> updateIdleVision(action)
                is Vision.Search -> updateSearchVision(action, currentVision)
                is Vision.Finished -> updateFinishedVision(action)
            }
        }

        private fun updateIdleVision(action: Action) {
            when (action) {
                is Action.Clear -> setVision(Vision.Idle)
                is Action.Search -> restartSearch(action.newSearch, "")
                is Action.Save -> Unit
            }
        }

        private fun restartSearch(newTerm: String, currentTerm: String) {
            val term = newTerm.trim()
            if (term.isNotEmpty() && term != currentTerm) {
                setVision(Vision.Search(searchTerm = term, candidates = emptyList()))
                stockCatalog.sendQuery(StockCatalogQuery.FindStock(term))
            }
        }

        override lateinit var stockCatalog: StockCatalog

        override fun onStockCatalogResult(result: StockCatalogResult) {
            when (result) {
                is StockCatalogResult.Samples -> setVision(
                    Vision.Search(
                        searchTerm = result.search,
                        candidates = result.samples.map {
                            Candidate(
                                assetSymbol = AssetSymbol(it.symbol.trim().toUpperCase()),
                                description = it.issuer,
                                marketWeight = MarketWeight(it.marketCapitalization)
                            )
                        })
                )
                is StockCatalogResult.InvalidSymbol -> Unit
                is StockCatalogResult.NetworkError -> Unit
                is StockCatalogResult.ParseError -> Unit
            }
        }

        private fun updateSearchVision(action: Action, currentVision: Vision.Search) {
            when (action) {
                is Action.Clear -> cancelSearch()
                is Action.Search -> restartSearch(action.newSearch, currentVision.searchTerm)
                is Action.Save -> endSearch(currentVision.candidates.firstOrNull())
            }
        }


        private fun cancelSearch() {
            stockCatalog.sendQuery(StockCatalogQuery.Clear)
            setVision(Vision.Idle)
        }

        private fun endSearch(chosenOne: Candidate?) {
            chosenOne?.let {
                val newRebellion = rebellionBook.value.addConstituent(it.assetSymbol, it.marketWeight)
                rebellionBook.write(newRebellion)
            }
            setVision(Vision.Finished)
        }

        private fun updateFinishedVision(action: Action) {
            when (action) {
                is Action.Clear -> setVision(Vision.Idle)
                is Action.Search -> Unit
                is Action.Save -> Unit
            }
        }
    }
}

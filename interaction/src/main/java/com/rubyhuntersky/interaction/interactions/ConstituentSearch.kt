package com.rubyhuntersky.interaction.interactions

import com.rubyhuntersky.data.Rebellion
import com.rubyhuntersky.data.assets.AssetSymbol
import com.rubyhuntersky.data.index.MarketWeight
import com.rubyhuntersky.interaction.BasicInteraction
import com.rubyhuntersky.interaction.books.Book

object ConstituentSearch {

    sealed class Vision {
        object Idle : Vision()
        data class Searching(val searchTerm: String, val results: List<Candidate>) : Vision()
        object Done : Vision()
    }

    data class Candidate(val symbol: AssetSymbol, val description: String, val marketWeight: MarketWeight)

    sealed class Action {
        object Clear : Action()
        data class Search(val searchTerm: String) : Action()
        data class Save(val assetSymbol: AssetSymbol, val marketWeight: MarketWeight) : Action()
    }

    class Interaction(private val rebellionBook: Book<Rebellion>) : BasicInteraction<Vision, Action>(
        startingVision = Vision.Idle, resetAction = Action.Clear
    ) {

        override fun update(action: Action) {
            val currentVision = this.vision
            when (currentVision) {
                is Vision.Idle -> updateIdle(action)
                is Vision.Searching -> updateSearching(action, currentVision)
                is Vision.Done -> updateDone(action)
            }
        }

        private fun updateDone(action: Action) {
            when (action) {
                is Action.Clear -> setVision(
                    Vision.Idle
                )
                is Action.Search -> Unit
                is Action.Save -> Unit
            }
        }

        private fun updateSearching(action: Action, searching: Vision.Searching) {
            when (action) {
                is Action.Clear -> setVision(
                    Vision.Idle
                )
                is Action.Search -> beginSearch(action.searchTerm, searching.searchTerm)
                is Action.Save -> endSearch(action.assetSymbol, action.marketWeight)
            }
        }

        private fun updateIdle(action: Action) {
            when (action) {
                is Action.Clear -> setVision(
                    Vision.Idle
                )
                is Action.Search -> beginSearch(action.searchTerm, "")
                is Action.Save -> Unit
            }
        }


        private fun endSearch(assetSymbol: AssetSymbol, marketWeight: MarketWeight) {
            rebellionBook.write(rebellionBook.reader.blockingFirst().addConstituent(assetSymbol, marketWeight))
            setVision(Vision.Done)
        }

        private fun beginSearch(newTerm: String, currentTerm: String) {
            val term = newTerm.trim()
            if (term.isNotEmpty() && term != currentTerm) {
                setVision(
                    Vision.Searching(
                        searchTerm = term,
                        results = emptyList()
                    )
                )
            }
        }
    }
}

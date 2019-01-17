package com.rubyhuntersky.interaction.interactions.constituentsearch

import com.rubyhuntersky.data.assets.AssetSymbol
import com.rubyhuntersky.data.index.MarketWeight
import com.rubyhuntersky.interaction.BasicInteraction
import com.rubyhuntersky.interaction.books.RebellionBook

class ConstituentSearchInteraction(private val rebellionBook: RebellionBook) :
    BasicInteraction<ConstituentSearchVision, ConstituentSearchAction>(
        startingVision = ConstituentSearchVision.Idle, resetAction = ConstituentSearchAction.Clear
    ) {

    override fun update(action: ConstituentSearchAction) {
        val currentVision = this.vision
        when (currentVision) {
            is ConstituentSearchVision.Idle -> updateIdle(action)
            is ConstituentSearchVision.Searching -> updateSearching(action, currentVision)
            is ConstituentSearchVision.Done -> updateDone(action)
        }
    }

    private fun updateDone(action: ConstituentSearchAction) {
        when (action) {
            is ConstituentSearchAction.Clear -> setVision(ConstituentSearchVision.Idle)
            is ConstituentSearchAction.Search -> Unit
            is ConstituentSearchAction.Save -> Unit
        }
    }

    private fun updateSearching(action: ConstituentSearchAction, searching: ConstituentSearchVision.Searching) {
        when (action) {
            is ConstituentSearchAction.Clear -> setVision(ConstituentSearchVision.Idle)
            is ConstituentSearchAction.Search -> beginSearch(action.searchTerm, searching.searchTerm)
            is ConstituentSearchAction.Save -> endSearch(action.assetSymbol, action.marketWeight)
        }
    }

    private fun updateIdle(action: ConstituentSearchAction) {
        when (action) {
            is ConstituentSearchAction.Clear -> setVision(ConstituentSearchVision.Idle)
            is ConstituentSearchAction.Search -> beginSearch(action.searchTerm, "")
            is ConstituentSearchAction.Save -> Unit
        }
    }


    private fun endSearch(assetSymbol: AssetSymbol, marketWeight: MarketWeight) {
        rebellionBook.write(rebellionBook.reader.blockingFirst().addConstituent(assetSymbol, marketWeight))
        setVision(ConstituentSearchVision.Done)
    }

    private fun beginSearch(newTerm: String, currentTerm: String) {
        val term = newTerm.trim()
        if (term.isNotEmpty() && term != currentTerm) {
            setVision(ConstituentSearchVision.Searching(searchTerm = term, results = emptyList()))
        }
    }
}
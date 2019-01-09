package com.rubyhuntersky.interaction.interactions.constituentsearch

import com.rubyhuntersky.data.assets.AssetSymbol
import com.rubyhuntersky.data.index.MarketWeight
import com.rubyhuntersky.interaction.Interaction
import com.rubyhuntersky.interaction.books.RebellionBook
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

sealed class ConstituentSearchVision {
    object Idle : ConstituentSearchVision()
    data class Searching(val searchTerm: String, val results: List<ConstituentSearchResult>) : ConstituentSearchVision()
    object Done : ConstituentSearchVision()
}

sealed class ConstituentSearchAction {
    object Clear : ConstituentSearchAction()
    data class Search(val searchTerm: String) : ConstituentSearchAction()
    data class Save(val assetSymbol: AssetSymbol, val marketWeight: MarketWeight) : ConstituentSearchAction()
}

data class ConstituentSearchResult(val symbol: AssetSymbol, val description: String, val marketWeight: MarketWeight)

class ConstituentSearchInteraction(private val rebellionBook: RebellionBook) :
    Interaction<ConstituentSearchVision, ConstituentSearchAction> {

    private val visionBehavior = BehaviorSubject.createDefault(ConstituentSearchVision.Idle as ConstituentSearchVision)
    private val vision: ConstituentSearchVision get() = visionBehavior.value!!
    private val visionWriter = visionBehavior.toSerialized()

    override val visionStream: Observable<ConstituentSearchVision>
        get() = visionBehavior.distinctUntilChanged()

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
            is ConstituentSearchAction.Clear -> visionWriter.onNext(ConstituentSearchVision.Idle)
            is ConstituentSearchAction.Search -> Unit
            is ConstituentSearchAction.Save -> Unit
        }
    }

    private fun updateSearching(action: ConstituentSearchAction, searching: ConstituentSearchVision.Searching) {
        when (action) {
            ConstituentSearchAction.Clear -> visionWriter.onNext(ConstituentSearchVision.Idle)
            is ConstituentSearchAction.Search -> beginSearch(action.searchTerm, searching.searchTerm)
            is ConstituentSearchAction.Save -> endSearch(action.assetSymbol, action.marketWeight)
        }
    }

    private fun updateIdle(action: ConstituentSearchAction) {
        when (action) {
            is ConstituentSearchAction.Clear -> visionWriter.onNext(ConstituentSearchVision.Idle)
            is ConstituentSearchAction.Search -> beginSearch(action.searchTerm, "")
            is ConstituentSearchAction.Save -> Unit
        }
    }

    private fun endSearch(assetSymbol: AssetSymbol, marketWeight: MarketWeight) {
        rebellionBook.write(rebellionBook.reader.blockingFirst().addConstituent(assetSymbol, marketWeight))
        visionWriter.onNext(ConstituentSearchVision.Done)
    }

    private fun beginSearch(newTerm: String, currentTerm: String) {
        val term = newTerm.trim()
        if (term.isNotEmpty() && term != currentTerm) {
            visionWriter.onNext(ConstituentSearchVision.Searching(term, emptyList()))
        }
    }
}
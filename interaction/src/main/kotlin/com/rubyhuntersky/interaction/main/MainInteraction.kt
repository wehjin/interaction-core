package com.rubyhuntersky.interaction.main

import com.rubyhuntersky.data.cash.CashEquivalent
import com.rubyhuntersky.data.report.Correction
import com.rubyhuntersky.data.report.CorrectionDetails
import com.rubyhuntersky.data.report.RebellionReport
import com.rubyhuntersky.interaction.core.Portal
import com.rubyhuntersky.interaction.core.NotImplementedPortal
import com.rubyhuntersky.interaction.books.RebellionBook
import com.rubyhuntersky.interaction.core.Interaction
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.subjects.BehaviorSubject


typealias MainVision = Vision
typealias MainAction = Action

sealed class Vision {
    object Loading : Vision()
    data class Viewing(val rebellionReport: RebellionReport) : Vision()
}

sealed class Action {
    object FindConstituent : Action()
    object OpenCashEditor : Action()
    data class OpenCorrectionDetails(val correction: Correction) : Action()
}

class MainInteraction(
    private val rebellionBook: RebellionBook,
    private val correctionDetailPortal: Portal<CorrectionDetails>,
    private val constituentSearchPortal: Portal<Unit> = NotImplementedPortal(),
    private val cashEditingPortal: Portal<Unit> = NotImplementedPortal()
) : Interaction<MainVision, MainAction> {

    private val visionSubject = BehaviorSubject.createDefault(Vision.Loading as MainVision)
    private val visionWriter = visionSubject.toSerialized()
    private val compositeDisposable = CompositeDisposable()

    private val vision: MainVision get() = visionSubject.value!!
    override val visionStream: Observable<MainVision> get() = visionSubject.distinctUntilChanged()

    init {
        rebellionBook.reader
            .subscribe { rebellion ->
                visionWriter.onNext(Vision.Viewing(RebellionReport(rebellion)))
            }
            .addTo(compositeDisposable)
    }

    override fun sendAction(action: MainAction) {
        val vision = this.vision
        when (vision) {
            is Vision.Loading -> updateLoading()
            is Vision.Viewing -> updateViewing(action)
        }
    }

    private fun updateViewing(action: MainAction) = when (action) {
        is Action.FindConstituent -> constituentSearchPortal.jump(Unit)
        is Action.OpenCashEditor -> cashEditingPortal.jump(Unit)
        is Action.OpenCorrectionDetails -> openCorrectionDetails(action.correction)
    }

    private fun openCorrectionDetails(correction: Correction) {
        val rebellion = rebellionBook.value
        val assetSymbol = correction.assetSymbol
        val constituent = rebellion.findConstituent(assetSymbol) ?: return
        val ownedShares = constituent.ownedShares
        val ownedValue = (constituent.cashEquivalent as? CashEquivalent.Amount)?.cashAmount ?: return
        val fullInvestment = (rebellion.fullInvestment as? CashEquivalent.Amount)?.cashAmount ?: return
        val targetValue = correction.targetValue(fullInvestment)
        val details = CorrectionDetails(assetSymbol, ownedShares, ownedValue, targetValue)
        correctionDetailPortal.jump(details)
    }

    private fun updateLoading() {}
}

package com.rubyhuntersky.interaction.interactions.main

import com.rubyhuntersky.data.report.Correction
import com.rubyhuntersky.data.report.RebellionReport
import com.rubyhuntersky.interaction.InteractionCatalyst
import com.rubyhuntersky.interaction.NotImplementedCatalyst
import com.rubyhuntersky.interaction.addTo
import com.rubyhuntersky.interaction.books.RebellionBook
import com.rubyhuntersky.interaction.interactions.common.Interaction
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject

class MainInteraction(
    rebellionBook: RebellionBook,
    private val correctionDetailCatalyst: InteractionCatalyst<Correction>,
    private val constituentSearchCatalyst: InteractionCatalyst<Unit> = NotImplementedCatalyst(),
    private val cashEditingCatalyst: InteractionCatalyst<Unit> = NotImplementedCatalyst()
) :
    Interaction<MainVision, MainAction> {

    private val visionSubject = BehaviorSubject.createDefault(MainVision.Loading as MainVision)
    private val visionWriter = visionSubject.toSerialized()
    private val compositeDisposable = CompositeDisposable()

    private val vision: MainVision get() = visionSubject.value!!
    override val visionStream: Observable<MainVision> get() = visionSubject.distinctUntilChanged()

    init {
        rebellionBook.reader
            .subscribe { rebellion ->
                visionWriter.onNext(MainVision.Viewing(RebellionReport(rebellion)))
            }
            .addTo(compositeDisposable)
    }

    override fun sendAction(action: MainAction) {
        val oldVision = this.vision
        when (oldVision) {
            is MainVision.Loading -> updateLoading()
            is MainVision.Viewing -> updateViewing(action)
        }
    }

    private fun updateViewing(action: MainAction) {
        when (action) {
            is MainAction.FindConstituent -> constituentSearchCatalyst.catalyze(Unit)
            is MainAction.OpenCashEditor -> cashEditingCatalyst.catalyze(Unit)
            is MainAction.OpenCorrectionDetails -> correctionDetailCatalyst.catalyze(action.correction)
        }
    }

    private fun updateLoading() {}
}

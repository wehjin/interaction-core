package com.rubyhuntersky.interaction.interactions.main

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
    private val correctionDetailCatalyst: InteractionCatalyst,
    private val constituentSearchCatalyst: InteractionCatalyst = NotImplementedCatalyst,
    private val cashEditingCatalyst: InteractionCatalyst = NotImplementedCatalyst
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

    override fun onAction(action: MainAction) {
        val oldVision = this.vision
        when (oldVision) {
            is MainVision.Loading -> updateLoading()
            is MainVision.Viewing -> updateViewing(action)
        }
    }

    private fun updateViewing(action: MainAction) {
        when (action) {
            MainAction.FindConstituent -> constituentSearchCatalyst.catalyze()
            MainAction.OpenCashEditor -> cashEditingCatalyst.catalyze()
            MainAction.OpenCorrectionDetails -> correctionDetailCatalyst.catalyze()
        }
    }

    private fun updateLoading() {}
}

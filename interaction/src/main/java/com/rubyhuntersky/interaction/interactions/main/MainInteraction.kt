package com.rubyhuntersky.interaction.interactions.main

import com.rubyhuntersky.data.report.RebellionReport
import com.rubyhuntersky.interaction.addTo
import com.rubyhuntersky.interaction.books.RebellionBook
import com.rubyhuntersky.interaction.interactions.symbolsearch.ConstituentSearchCatalyst
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject

class MainInteraction(
    rebellionBook: RebellionBook,
    private val constituentSearchCatalyst: ConstituentSearchCatalyst
) {

    sealed class Vision {
        object Loading : Vision()
        data class Viewing(val rebellionReport: RebellionReport) : Vision()
    }

    sealed class Action {
        object FindConstituent : Action()
    }

    private val visionSubject = BehaviorSubject.createDefault(Vision.Loading as Vision)
    private val visionWriter = visionSubject.toSerialized()
    private val compositeDisposable = CompositeDisposable()

    private val vision: Vision get() = visionSubject.value!!
    val visionStream: Observable<Vision> get() = visionSubject.distinctUntilChanged()

    init {
        rebellionBook.reader
            .subscribe { rebellion ->
                visionWriter.onNext(Vision.Viewing(RebellionReport(rebellion)))
            }
            .addTo(compositeDisposable)
    }

    fun update(action: Action) {
        val oldVision = this.vision
        when (oldVision) {
            is Vision.Loading -> updateLoading()
            is Vision.Viewing -> updateViewing(action)
        }
    }

    private fun updateViewing(action: Action) {
        when (action) {
            Action.FindConstituent -> constituentSearchCatalyst.catalyze()
        }
    }

    private fun updateLoading() {}
}

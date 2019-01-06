package com.rubyhuntersky.interaction.interactions.main

import com.rubyhuntersky.data.report.RebellionReport
import com.rubyhuntersky.interaction.addTo
import com.rubyhuntersky.interaction.books.RebellionBook
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject

class MainInteraction(private val rebellionBook: RebellionBook) {

    sealed class State {
        object Loading : State()
        data class Viewing(val rebellionReport: RebellionReport) : State()
    }

    private val stateSubject = BehaviorSubject.createDefault(State.Loading as State)
    private val stateWriter = stateSubject.toSerialized()
    private val compositeDisposable = CompositeDisposable()

    val state: Observable<State> get() = stateSubject.distinctUntilChanged()

    init {
        rebellionBook.reader
            .subscribe { rebellion -> stateWriter.onNext(State.Viewing(RebellionReport(rebellion))) }
            .addTo(compositeDisposable)
    }
}

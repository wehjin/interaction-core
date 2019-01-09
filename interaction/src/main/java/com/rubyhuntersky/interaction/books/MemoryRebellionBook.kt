package com.rubyhuntersky.interaction.books

import com.rubyhuntersky.data.Rebellion
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

class MemoryRebellionBook : RebellionBook {

    private val rebellionSubject: BehaviorSubject<Rebellion> = BehaviorSubject.createDefault(Rebellion.SEED)
    private val rebellionWriter = rebellionSubject.toSerialized()

    override val reader: Observable<Rebellion> get() = rebellionSubject.distinctUntilChanged()
    override fun write(rebellion: Rebellion) = rebellionWriter.onNext(rebellion)
}
package com.rubyhuntersky.interaction.cashediting

import com.rubyhuntersky.data.Rebellion
import com.rubyhuntersky.data.cash.CashAmount
import com.rubyhuntersky.interaction.books.Book
import com.rubyhuntersky.interaction.common.Interaction
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

sealed class Vision {
    object Editing : Vision()
    object Done : Vision()
}

sealed class Action {
    object SaveCashChange : Action()
}

class CashEditing(private val rebellionBook: Book<Rebellion>) :
    Interaction<Vision, Action> {

    private val visionBehavior = BehaviorSubject.createDefault(Vision.Editing as Vision)
    private val visionWriter = visionBehavior.toSerialized()

    override val visionStream: Observable<Vision> get() = visionBehavior.distinctUntilChanged()

    override fun sendAction(action: Action) {
        val vision = visionBehavior.value!!
        when (vision) {
            is Vision.Editing -> updateEditing(action)
            is Vision.Done -> Unit
        }
    }

    private fun updateEditing(action: Action) {
        when (action) {
            is Action.SaveCashChange -> {
                val newRebellion = rebellionBook.value.setNewInvestment(CashAmount(10000))
                rebellionBook.write(newRebellion)
                visionWriter.onNext(Vision.Done)
            }
        }
    }

    override fun reset() {
        visionBehavior.onNext(Vision.Editing)
    }
}
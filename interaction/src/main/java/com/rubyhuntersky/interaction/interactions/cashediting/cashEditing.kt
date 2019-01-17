package com.rubyhuntersky.interaction.interactions.cashediting

import com.rubyhuntersky.data.Rebellion
import com.rubyhuntersky.data.cash.CashAmount
import com.rubyhuntersky.interaction.interactions.common.Interaction
import com.rubyhuntersky.interaction.books.Book
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

sealed class CashEditingVision {
    object Editing : CashEditingVision()
    object Done : CashEditingVision()
}

sealed class CashEditingAction {
    object SaveCashChange : CashEditingAction()
}

class CashEditingInteraction(private val rebellionBook: Book<Rebellion>) :
    Interaction<CashEditingVision, CashEditingAction> {

    private val visionBehavior = BehaviorSubject.createDefault(CashEditingVision.Editing as CashEditingVision)
    private val visionWriter = visionBehavior.toSerialized()

    override val visionStream: Observable<CashEditingVision> get() = visionBehavior.distinctUntilChanged()

    override fun onAction(action: CashEditingAction) {
        val vision = visionBehavior.value!!
        when (vision) {
            is CashEditingVision.Editing -> updateEditing(action)
            is CashEditingVision.Done -> Unit
        }
    }

    private fun updateEditing(action: CashEditingAction) {
        when (action) {
            is CashEditingAction.SaveCashChange -> {
                val newRebellion = rebellionBook.value.setNewInvestment(CashAmount(10000))
                rebellionBook.write(newRebellion)
                visionWriter.onNext(CashEditingVision.Done)
            }
        }
    }

    override fun reset() {
        visionBehavior.onNext(CashEditingVision.Editing)
    }
}
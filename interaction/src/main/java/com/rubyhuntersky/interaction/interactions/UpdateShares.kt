package com.rubyhuntersky.interaction.interactions

import com.rubyhuntersky.data.assets.OwnedAsset
import com.rubyhuntersky.data.assets.ShareCount
import com.rubyhuntersky.data.assets.SharePrice
import com.rubyhuntersky.data.cash.CashAmount
import com.rubyhuntersky.interaction.BehaviorInteraction
import com.rubyhuntersky.interaction.books.ConstituentBook
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import java.util.*
import com.rubyhuntersky.interaction.interactions.common.Interaction as CommonInteraction


object UpdateShares {

    sealed class Vision {
        object Loading : Vision()

        data class Prompt(
            val ownedCount: Int,
            val sharePrice: SharePrice,
            val newPrice: String?,
            val shouldUpdateCash: Boolean,
            val numberDelta: NumberDelta,
            val canUpdate: Boolean
        ) : Vision()

        object Dismissed : Vision()
    }

    sealed class Action {
        object Reset : Action()
        data class Load(val ownedAsset: OwnedAsset) : Action()
        data class ShouldUpdateCash(val shouldUpdateCash: Boolean) : Action()
        data class NewPrice(val newSharePrice: String) : Action()
        data class NewTotalCount(val newTotalCount: String) : Action()
        data class NewChangeCount(val newChangeCount: String) : Action()
        data class Save(val date: Date) : Action()
    }


    class Interaction(private val constituentBook: ConstituentBook) :
        BehaviorInteraction<Vision, Action>(startingVision = Vision.Loading, resetAction = Action.Reset) {

        // TODO Delete these and build Prompt from Prompt
        private var ownedAsset: OwnedAsset? = null
        private var shouldUpdateCash = true
        private var newTotal = ""
        private var newChange = ""
        private var newPrice = ""

        private val composite = CompositeDisposable()

        override fun sendAction(action: Action) {
            when (action) {
                is Action.Reset -> {
                    setVision(Vision.Loading)
                    composite.clear()
                    constituentBook.reader.subscribe {
                        val asset = OwnedAsset(it.assetSymbol, it.ownedShares, it.sharePrice)
                        sendAction(Action.Load(asset))
                    }.addTo(composite)
                }
                is Action.Load -> startPrompt(action)
                is Action.ShouldUpdateCash -> evolvePrompt { shouldUpdateCash = action.shouldUpdateCash }
                is Action.NewPrice -> evolvePrompt { newPrice = action.newSharePrice }
                is Action.NewTotalCount -> evolvePrompt { newTotal = action.newTotalCount }
                is Action.NewChangeCount -> evolvePrompt { newChange = action.newChangeCount }
                is Action.Save -> dismissPrompt(action.date)
            }
        }

        private fun startPrompt(action: Action.Load) = when (vision) {
            is Vision.Loading, is Vision.Prompt -> {
                ownedAsset = action.ownedAsset
                shouldUpdateCash = true
                newTotal = ""
                newChange = ""
                newPrice = ""
                sendPromptVision()
            }
            is Vision.Dismissed -> Unit
        }

        private fun dismissPrompt(date: Date) = when (vision) {
            is Vision.Prompt -> {
                if (canUpdate) {
                    val newPriceDouble = newPrice.toDouble()
                    constituentBook.updateShareCountPriceAndCash(
                        assetSymbol = ownedAsset!!.assetSymbol,
                        shareCount = numberDelta.toShareCount(ownedAsset!!.shareCount),
                        sharePrice = SharePrice.Sample(CashAmount(newPriceDouble), date),
                        cashChange = if (shouldUpdateCash) {
                            val shareDelta = numberDelta.toShareDelta(ownedAsset!!.shareCount)
                            CashAmount(newPriceDouble * shareDelta.value * -1)
                        } else {
                            null
                        }
                    )
                }
                composite.clear()
                setVision(Vision.Dismissed)
            }
            is Vision.Loading, is Vision.Dismissed -> Unit
        }

        private fun evolvePrompt(evolve: () -> Unit) = when (vision) {
            is Vision.Loading -> Unit
            is Vision.Prompt -> {
                evolve()
                sendPromptVision()
            }
            is Vision.Dismissed -> Unit
        }

        private fun sendPromptVision() = setVision(

            Vision.Prompt(
                ownedCount = ownedAsset!!.shareCount.toDouble().toInt(),
                sharePrice = ownedAsset!!.sharePrice,
                newPrice = newPrice,
                shouldUpdateCash = this.shouldUpdateCash,
                numberDelta = numberDelta,
                canUpdate = canUpdate
            )
        )

        private val numberDelta: NumberDelta
            get() = when {
                newTotal.isNotBlank() -> NumberDelta.Total(newTotal)
                newChange.isNotBlank() -> NumberDelta.Change(newChange)
                else -> NumberDelta.Undecided
            }

        private val canUpdate: Boolean
            get() = newPrice.toDoubleOrNull() != null && numberDelta.isValid
    }

    sealed class NumberDelta {

        object Undecided : NumberDelta()

        data class Total(val newTotal: String) : NumberDelta()

        data class Change(val newChange: String) : NumberDelta()

        val isValid: Boolean
            get() = when (this) {
                is Undecided -> false
                is Total -> newTotal.toLongOrNull()?.let { it >= 0 } ?: false
                is Change -> newChange.toLongOrNull() != null
            }

        fun toShareCount(shareCount: ShareCount): ShareCount = when (this) {
            is Undecided -> shareCount
            is Total -> ShareCount(newTotal.toLong())
            is Change -> shareCount + ShareCount(newChange.toDouble())
        }

        fun toShareDelta(shareCount: ShareCount): ShareCount = when (this) {
            is Undecided -> ShareCount.ZERO
            is Total -> ShareCount(newTotal.toDouble()) - shareCount
            is Change -> ShareCount(newChange.toDouble())
        }
    }
}
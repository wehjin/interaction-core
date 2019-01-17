package com.rubyhuntersky.interaction.interactions.cashediting

import com.rubyhuntersky.data.cash.CashAmount
import com.rubyhuntersky.interaction.books.MemoryRebellionBook
import org.junit.Assert.assertTrue
import org.junit.Test

class CashEditingInteractionTest {

    private val rebellionBook = MemoryRebellionBook()
    private val cashEditingInteraction = CashEditingInteraction(rebellionBook)

    @Test
    fun startingVisionIsEditing() {
        cashEditingInteraction.visionStream.test().assertValue(CashEditingVision.Editing)
    }

    @Test
    fun saveActionWritesRebellionToBook() {
        cashEditingInteraction.onAction(CashEditingAction.SaveCashChange)
        assertTrue(rebellionBook.value.newInvestment != CashAmount.ZERO)
    }

    @Test
    fun saveActionSetVisionToDone() {
        cashEditingInteraction.onAction(CashEditingAction.SaveCashChange)
        cashEditingInteraction.visionStream.test().assertValue(CashEditingVision.Done)
    }

    @Test
    fun resetAfterSaveSetsVisionToEditing() {
        cashEditingInteraction.onAction(CashEditingAction.SaveCashChange)
        cashEditingInteraction.reset()
        cashEditingInteraction.visionStream.test().assertValue(CashEditingVision.Editing)
    }
}
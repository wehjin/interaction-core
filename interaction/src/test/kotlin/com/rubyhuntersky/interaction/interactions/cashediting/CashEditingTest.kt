package com.rubyhuntersky.interaction.interactions.cashediting

import com.rubyhuntersky.data.cash.CashAmount
import com.rubyhuntersky.interaction.books.MemoryRebellionBook
import com.rubyhuntersky.interaction.cashediting.Action
import com.rubyhuntersky.interaction.cashediting.CashEditing
import com.rubyhuntersky.interaction.cashediting.Vision
import org.junit.Assert.assertTrue
import org.junit.Test

class CashEditingTest {

    private val rebellionBook = MemoryRebellionBook()
    private val cashEditingInteraction = CashEditing(rebellionBook)

    @Test
    fun startingVisionIsEditing() {
        cashEditingInteraction.visionStream.test().assertValue(Vision.Editing)
    }

    @Test
    fun saveActionWritesRebellionToBook() {
        cashEditingInteraction.sendAction(Action.SaveCashChange)
        assertTrue(rebellionBook.value.newInvestment != CashAmount.ZERO)
    }

    @Test
    fun saveActionSetVisionToDone() {
        cashEditingInteraction.sendAction(Action.SaveCashChange)
        cashEditingInteraction.visionStream.test().assertValue(Vision.Done)
    }

    @Test
    fun resetAfterSaveSetsVisionToEditing() {
        cashEditingInteraction.sendAction(Action.SaveCashChange)
        cashEditingInteraction.reset()
        cashEditingInteraction.visionStream.test().assertValue(Vision.Editing)
    }
}
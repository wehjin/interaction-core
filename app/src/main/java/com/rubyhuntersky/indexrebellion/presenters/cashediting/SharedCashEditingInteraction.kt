package com.rubyhuntersky.indexrebellion.presenters.cashediting

import com.rubyhuntersky.indexrebellion.books.SharedRebellionBook
import com.rubyhuntersky.interaction.cashediting.Action
import com.rubyhuntersky.interaction.cashediting.CashEditing
import com.rubyhuntersky.interaction.cashediting.Vision
import com.rubyhuntersky.interaction.core.Interaction

object SharedCashEditingInteraction : Interaction<Vision, Action>
by CashEditing(SharedRebellionBook)
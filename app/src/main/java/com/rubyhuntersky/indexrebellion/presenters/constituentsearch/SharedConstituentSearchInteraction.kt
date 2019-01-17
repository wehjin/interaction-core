package com.rubyhuntersky.indexrebellion.presenters.constituentsearch

import com.rubyhuntersky.indexrebellion.books.SharedRebellionBook
import com.rubyhuntersky.interaction.interactions.ConstituentSearch
import com.rubyhuntersky.interaction.interactions.common.Interaction

object SharedConstituentSearchInteraction : Interaction<ConstituentSearch.Vision, ConstituentSearch.Action>
by ConstituentSearch.Interaction(rebellionBook = SharedRebellionBook)

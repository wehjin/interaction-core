package com.rubyhuntersky.indexrebellion.books

import com.rubyhuntersky.interaction.books.MemoryRebellionBook
import com.rubyhuntersky.interaction.books.RebellionBook

object SharedRebellionBook : RebellionBook by MemoryRebellionBook()
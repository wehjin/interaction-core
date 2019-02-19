package com.rubyhuntersky.interaction.books

import com.rubyhuntersky.data.Rebellion
import com.rubyhuntersky.interaction.core.BehaviorBook
import com.rubyhuntersky.interaction.core.Book

class MemoryRebellionBook : RebellionBook, Book<Rebellion> by BehaviorBook(
    Rebellion.SEED
)

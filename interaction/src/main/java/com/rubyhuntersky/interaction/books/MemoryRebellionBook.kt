package com.rubyhuntersky.interaction.books

import com.rubyhuntersky.data.Rebellion

class MemoryRebellionBook : RebellionBook, Book<Rebellion> by SubjectBook(Rebellion.SEED)

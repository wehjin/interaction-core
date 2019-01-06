package com.rubyhuntersky.interaction.books

import com.rubyhuntersky.data.Rebellion
import io.reactivex.Observable

interface RebellionBook {

    val reader: Observable<Rebellion>
    fun write(rebellion: Rebellion)
}
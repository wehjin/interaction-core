package com.rubyhuntersky.interaction.books

import com.rubyhuntersky.data.assets.AssetSymbol
import com.rubyhuntersky.data.assets.ShareCount
import com.rubyhuntersky.data.assets.SharePrice
import com.rubyhuntersky.data.cash.CashAmount
import com.rubyhuntersky.data.index.Constituent
import io.reactivex.Observable

class ConstituentBook(private val rebellionBook: RebellionBook, private val assetSymbol: AssetSymbol) :
    Book<Constituent> {

    override val reader: Observable<Constituent>
        get() = rebellionBook.reader
            .switchMap { rebellion ->
                val constituent = rebellion.index.constituents.find { it.assetSymbol == assetSymbol }
                constituent?.let { Observable.just(it) } ?: Observable.never()
            }
            .distinctUntilChanged()

    override fun write(value: Constituent) {
        rebellionBook.updateConstituent(value)
    }

    fun updateShareCountPriceAndCash(
        assetSymbol: AssetSymbol,
        shareCount: ShareCount,
        sharePrice: SharePrice,
        cashChange: CashAmount?
    ) = rebellionBook.updateShareCountPriceAndCash(assetSymbol, shareCount, sharePrice, cashChange)
}



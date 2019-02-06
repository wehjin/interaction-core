package com.rubyhuntersky.indexrebellion.common

import android.app.Application
import com.rubyhuntersky.data.assets.AssetSymbol
import com.rubyhuntersky.data.assets.SharePrice
import com.rubyhuntersky.data.index.Constituent
import com.rubyhuntersky.data.index.MarketWeight
import com.rubyhuntersky.indexrebellion.books.SharedRebellionBook
import kotlinx.serialization.json.Json

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // TODO Figure out why this is needed.
        // Without it, we get a runtime error about inability to find SharePrice.Unknown.serializer
        // after opening SharedRebellionBook.
        // - Calling stringify on SharePrice.Unknown alone still gets the error.
        // - Calling just Constituent.serializer() still gets the error.
        // - Calling SharePrice.Unknown() by itself still gets the error.
        Json.stringify(
            Constituent.serializer(),
            Constituent(AssetSymbol("DUMMY"), MarketWeight.TEN, SharePrice.Unknown())
        )
        SharedRebellionBook.open(this)
    }
}
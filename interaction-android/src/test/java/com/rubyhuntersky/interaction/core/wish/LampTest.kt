package com.rubyhuntersky.interaction.core.wish

import io.reactivex.Single
import org.junit.Assert.assertNotNull
import org.junit.Test

class LampTest {

    interface Thingy {
        val x: String
    }

    class AThingy : Thingy {
        override val x: String = "A"
    }

    object ThingyGenie : Genie<Thingy, String> {

        override val paramsClass: Class<Thingy> = Thingy::class.java
        override fun toSingle(params: Thingy): Single<String> = Single.fromCallable { params.x }
    }

    private val lamp = Lamp()

    @Test
    fun lampFindsInterface() {
        lamp.add(ThingyGenie)
        val single = lamp.toSingle(
            AThingy(),
            WishKind.One<String, String>({ "Result: $it" }, { "Error: ${it.localizedMessage}" })
        )
        assertNotNull(single)
    }

    @Test
    fun genieParams() {

        data class FormatNumber(val n: Int) : GenieParams<String>

        lamp.add(object : Genie<FormatNumber, String> {
            override val paramsClass: Class<FormatNumber> = FormatNumber::class.java
            override fun toSingle(params: FormatNumber): Single<String> = Single.just(params.n.toString())
        })

        data class Print(val string: String)

        val wish = FormatNumber(3)
            .toWish<FormatNumber, Print>(
                name = "format-number",
                onResult = ::Print,
                onError = { throw it }
            )

        lamp.toSingle(wish).test().assertValue(Print("3"))
    }
}
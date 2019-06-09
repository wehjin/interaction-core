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
}
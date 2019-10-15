package com.rubyhuntersky.seismic_stampede.gather.core

import org.junit.Test
import kotlin.test.assertEquals

class GatherCoreKtTest {

    @Test
    fun countIsLengthOfGatherChain() {
        val gather =
            gatherOf("Location", validator = ::validWhenNotEmpty)
                .and("Username", validator = ::validWhenNotEmpty)
        assertEquals(2, gather.count)
    }
}
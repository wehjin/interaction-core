package com.rubyhuntersky.seismic_stampede

import org.junit.Assert
import org.junit.Test

class ExtensionsKtTest {

    @Test
    fun readWriteInt() {
        val i = 0x1234FF78
        val bytes = ByteArray(Int.SIZE_BYTES)
        bytes.writeInt(i)
        val j = bytes.readInt()
        Assert.assertEquals(i, j)
    }
}
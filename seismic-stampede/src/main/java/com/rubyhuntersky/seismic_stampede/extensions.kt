package com.rubyhuntersky.seismic_stampede

fun ByteArray.toHex(): String = joinToString("") { "%02x".format(it) }
fun ByteArray.zero() = indices.forEach { i -> this[i] = 0 }

fun ByteArray.writeInt(intValue: Int) {
    (Int.SIZE_BYTES - 1 downTo 0).forEach { bigEnd ->
        val arrayIndex = (Int.SIZE_BYTES - 1) - bigEnd
        this[arrayIndex] = (intValue shr (8 * bigEnd)).toByte()
    }
}

fun ByteArray.readInt(): Int {
    var value = 0
    val mask = intArrayOf(0x7f000000, 0x00ff0000, 0x0000ff00, 0x000000ff)
    (Int.SIZE_BYTES - 1 downTo 0).forEach { bigEnd ->
        val arrayIndex = (Int.SIZE_BYTES - 1) - bigEnd
        value = value or (this[arrayIndex].toInt() shl (8 * bigEnd) and mask[arrayIndex])
    }
    return value
}

fun String.hexToByteArray(): ByteArray = ByteArray(length / 2)
    .also { bytes ->
        (0 until length step 2).forEach { i ->
            val left = Character.digit(this[i], 16) shl 4
            val right = Character.digit(this[i + 1], 16)
            bytes[i / 2] = (left + right).toByte()
        }
    }




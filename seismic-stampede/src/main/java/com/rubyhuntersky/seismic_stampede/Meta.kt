package com.rubyhuntersky.seismic_stampede

import java.util.*

fun makeMeta(type: String, fields: List<String>): ByteArray {
    val encoder = Base64.getEncoder()
    val fields64 = fields.map { encoder.encodeToString(it.toByteArray(Charsets.UTF_8)) }
    return (listOf(type) + fields64).joinToString("\n").toByteArray(Charsets.UTF_8)
}

fun breakMeta(metaBytes: ByteArray): List<String> {
    val decoder = Base64.getDecoder()
    val metaParts = String(metaBytes).split("\n")
    val type = metaParts[0]
    val fields64 = metaParts.slice(1..metaParts.lastIndex)
    val fields = fields64.map { String(decoder.decode(it)) }
    return listOf(type) + fields
}


fun makeRecord(metaBytes: ByteArray, bodyBytes: ByteArray): ByteArray {
    val metaLength = metaBytes.size
    return ByteArray(Int.SIZE_BYTES + metaBytes.size + bodyBytes.size).also { bytes ->
        val metaIndex = Int.SIZE_BYTES
        val bodyIndex = metaIndex + metaBytes.size
        bytes.writeInt(metaLength)
        System.arraycopy(metaBytes, 0, bytes, metaIndex, metaBytes.size)
        System.arraycopy(bodyBytes, 0, bytes, bodyIndex, bodyBytes.size)
    }
}

fun breakRecordToMeta(plainBytes: ByteArray): ByteArray {
    val metaSize = plainBytes.readInt()
    val metaIndex = Int.SIZE_BYTES
    return ByteArray(metaSize).also {
        System.arraycopy(plainBytes, metaIndex, it, 0, metaSize)
    }
}


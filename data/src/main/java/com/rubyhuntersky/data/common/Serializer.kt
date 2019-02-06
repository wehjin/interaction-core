package com.rubyhuntersky.data.common

import kotlinx.serialization.*
import kotlinx.serialization.internal.StringDescriptor
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.*

@Serializer(forClass = BigDecimal::class)
object BigDecimalSerializer : KSerializer<BigDecimal> {

    override val descriptor: SerialDescriptor = StringDescriptor.withName("java.math.BigDecimal")

    override fun serialize(encoder: Encoder, obj: BigDecimal) {
        encoder.encodeString(obj.toDouble().toString())
    }

    override fun deserialize(decoder: Decoder): BigDecimal {
        return BigDecimal(decoder.decodeString())
    }
}

@Serializer(forClass = Date::class)
object DateSerializer : KSerializer<Date> {
    private val df = SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS", Locale.US)

    override val descriptor: SerialDescriptor = StringDescriptor.withName("java.util.Date")

    override fun serialize(encoder: Encoder, obj: Date) {
        encoder.encodeString(df.format(obj))
    }

    override fun deserialize(decoder: Decoder): Date {
        return df.parse(decoder.decodeString())
    }
}
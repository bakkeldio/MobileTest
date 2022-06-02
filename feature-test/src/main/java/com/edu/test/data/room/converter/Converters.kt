package com.edu.test.data.room.converter

import androidx.room.TypeConverter
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import java.util.*

object Converters {

    @TypeConverter
    fun fromDate(value: Date) = Json.encodeToString(DateSerializer, value)

    @TypeConverter
    fun toDate(value: String) = Json.decodeFromString(DateSerializer, value)

    object DateSerializer : KSerializer<Date> {
        override fun deserialize(decoder: Decoder): Date {
            return Date(decoder.decodeLong())
        }

        override val descriptor: SerialDescriptor
            get() = PrimitiveSerialDescriptor("Date", PrimitiveKind.LONG)

        override fun serialize(encoder: Encoder, value: Date) {
            encoder.encodeLong(value.time)
        }

    }
}
package com.example.thdt_quangtran.utils

import androidx.room.TypeConverter
import java.util.Date

class Converters {
    @TypeConverter
    fun fromByteArray(value: ByteArray?): String? {
        return value?.let { String(it) }
    }

    @TypeConverter
    fun toByteArray(value: String?): ByteArray? {
        return value?.toByteArray()
    }

    @TypeConverter
    fun fromDate(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun toDate(timestamp: Long?): Date? {
        return timestamp?.let { Date(it) }
    }
}
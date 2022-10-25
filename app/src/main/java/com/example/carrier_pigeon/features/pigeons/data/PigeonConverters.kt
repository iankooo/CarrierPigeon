package com.example.carrier_pigeon.features.pigeons.data

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PigeonConverters {

    @TypeConverter
    fun fromGroupTaskMemberList(value: Pigeon?): String {
        val gson = Gson()
        val type = object : TypeToken<Pigeon>() {}.type
        return gson.toJson(value, type)
    }

    @TypeConverter
    fun toGroupTaskMemberList(value: String): Pigeon? {
        val gson = Gson()
        val type = object : TypeToken<Pigeon>() {}.type
        return gson.fromJson(value, type)
    }
}

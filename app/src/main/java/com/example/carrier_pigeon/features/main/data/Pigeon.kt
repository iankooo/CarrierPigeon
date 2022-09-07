package com.example.carrier_pigeon.features.main.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.jetbrains.annotations.NotNull

@Entity(tableName = "pigeon_table")
data class Pigeon(
    @PrimaryKey
    @NotNull
    val id: String,
    val gender: String,
    val country: String,
    val nickname: String,
    val color: String,
    val details: String,
    val pigeonImage: String?,
    val pigeonEyeImage: String?
)

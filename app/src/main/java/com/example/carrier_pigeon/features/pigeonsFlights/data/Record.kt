package com.example.carrier_pigeon.features.pigeonsFlights.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import org.jetbrains.annotations.NotNull

@Entity(tableName = "record_table")
@Parcelize
data class Record(
    @PrimaryKey(autoGenerate = true)
    @NotNull
    var id: Int = 0,
    var country: String,
    var dateOfBirth: String,
    var series: String,
    var gender: String,
    var color: String,
    var firstVaccine: Int,
    val secondVaccine: Int,
    val thirdVaccine: Int
) : Parcelable

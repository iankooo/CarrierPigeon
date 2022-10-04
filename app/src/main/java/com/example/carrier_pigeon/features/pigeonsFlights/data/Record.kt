package com.example.carrier_pigeon.features.pigeonsFlights.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.carrier_pigeon.features.pigeons.data.Pigeon
import kotlinx.parcelize.Parcelize
import org.jetbrains.annotations.NotNull

@Entity(
    tableName = "record_table",
    foreignKeys = [
        ForeignKey(
            entity = Pigeon::class,
            parentColumns = ["id"],
            childColumns = ["pigeonId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
@Parcelize
data class Record(
    @PrimaryKey(autoGenerate = true)
    @NotNull
    var id: Int = 0,
    var pigeonId: Int,
    var country: String,
    var dateOfBirth: String,
    var series: String,
    var gender: String,
    var color: String,
    var firstVaccine: Int,
    val secondVaccine: Int,
    val thirdVaccine: Int
) : Parcelable

package com.example.carrier_pigeon.features.pigeons.data

import android.os.Parcelable
import androidx.room.*
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import org.jetbrains.annotations.NotNull

@Entity(tableName = "pigeon_table")
@Parcelize
data class Pigeon(
    @PrimaryKey(autoGenerate = true)
    @NotNull
    var id: Int = 0,
    val series: String,
    val gender: String,
    val country: String,
    val nickname: String,
    val color: String,
    val details: String,
    val pigeonImage: String?,
    val pigeonEyeImage: String?,
    val dateOfBirth: String?,
    val firstVaccine: Int,
    val secondVaccine: Int,
    val thirdVaccine: Int,
    val familyTreeId: Int,
    var motherPigeon: Int?,
    var fatherPigeon: Int?
) : Parcelable {
    @IgnoredOnParcel
    @Ignore
    var children: List<Pigeon> = emptyList()
}

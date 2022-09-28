package com.example.carrier_pigeon.features.families.data

import android.os.Parcelable
import com.example.carrier_pigeon.features.pigeons.data.Pigeon
import kotlinx.parcelize.Parcelize

@Parcelize
data class Family(
    val mother: Pigeon,
    val father: Pigeon
) : Parcelable

package com.example.carrier_pigeon.features.pigeons.addPigeon

import android.os.Parcelable
import com.example.carrier_pigeon.features.pigeons.data.Pigeon
import kotlinx.parcelize.Parcelize

@Parcelize
data class AncestorDescendantBundle(
    val newPigeon: Pigeon?,
    val existingPigeon: Pigeon?,
    val depth: Int
) : Parcelable

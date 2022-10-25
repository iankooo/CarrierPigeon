package com.example.carrier_pigeon.features.pigeons.detailPigeon

import android.os.Parcelable
import com.example.carrier_pigeon.features.pigeons.data.Pigeon
import kotlinx.parcelize.Parcelize

@Parcelize
data class AncestorDescendantBundle(
    val newPigeon: Pigeon?,
    var existingPigeon: Pigeon?,
    val depth: Int
) : Parcelable

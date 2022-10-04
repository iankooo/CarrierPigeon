package com.example.carrier_pigeon.features.pigeons.detailPigeon

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.carrier_pigeon.features.pigeons.data.Pigeon
import org.jetbrains.annotations.NotNull

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Pigeon::class,
            parentColumns = ["id"],
            childColumns = ["ancestorId"],
            onDelete = ForeignKey.CASCADE
        ), ForeignKey(
            entity = Pigeon::class,
            parentColumns = ["id"],
            childColumns = ["descendantId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class AncestorDescendant(
    @PrimaryKey(autoGenerate = true)
    var ancestorDescendantId: Int = 0,

    @NotNull
    val ancestorId: Int?,

    @NotNull
    val descendantId: Int?,

    @NotNull
    val depth: Int
)

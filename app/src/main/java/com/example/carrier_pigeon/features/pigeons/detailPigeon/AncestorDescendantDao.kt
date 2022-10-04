package com.example.carrier_pigeon.features.pigeons.addPigeon

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface AncestorDescendantDao {
    @Insert
    suspend fun insert(ancestorDescendant: AncestorDescendant?)

    @Query("DELETE FROM AncestorDescendant")
    fun deleteAll()

    @Query("SELECT * FROM AncestorDescendant")
    fun getAllAncestorDescendants(): LiveData<List<AncestorDescendant?>?>?

    @Query("SELECT * FROM AncestorDescendant WHERE AncestorId = :ancestorId")
    fun getAncestorDescendantsByAncestorId(ancestorId: Int): LiveData<List<AncestorDescendant?>?>?

    @Query("SELECT * FROM AncestorDescendant WHERE AncestorId = :ancestorId")
    fun getAllAncestorDescendantRecordsByAncestorIdNotLive(ancestorId: Int): List<AncestorDescendant?>?

    @Query("SELECT * FROM AncestorDescendant")
    fun getAllAncestorDescendantRecordsNotLive(): List<AncestorDescendant?>?
}

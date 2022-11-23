package com.example.carrier_pigeon.features.pigeons.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.carrier_pigeon.features.pigeons.data.Pigeon

@Dao
interface PigeonDao {
    @Insert
    suspend fun insert(pigeon: Pigeon)

    @Update
    suspend fun update(pigeon: Pigeon)

    @Update
    suspend fun delete(pigeon: Pigeon)

    @Query("DELETE FROM pigeon_table")
    fun deleteAll()

    @Query("SELECT * FROM `pigeon_table` WHERE isDeleted=:isDeleted")
    fun fetchAllPigeons(isDeleted: Boolean = false): LiveData<List<Pigeon>>

    @Query("SELECT * FROM `pigeon_table` WHERE id=:id")
    fun fetchPigeonById(id: Int): Pigeon?

    @Query("SELECT COUNT(*) from pigeon_table")
    fun countPigeons(): Int
}

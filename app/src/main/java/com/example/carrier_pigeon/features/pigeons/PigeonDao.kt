package com.example.carrier_pigeon.features.pigeons

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import com.example.carrier_pigeon.features.pigeons.data.Pigeon

@Dao
interface PigeonDao {
    @Insert
    suspend fun insert(pigeon: Pigeon)

    @Update
    suspend fun update(pigeon: Pigeon)

    @Delete
    suspend fun delete(pigeon: Pigeon)

    @Query("DELETE FROM pigeon_table")
    fun deleteAll()

    @Query("SELECT * FROM `pigeon_table`")
    fun fetchAllPigeons(): Flow<List<Pigeon>>

    @Query("SELECT * FROM `pigeon_table` WHERE id=:id")
    fun fetchPigeonById(id: Int): Flow<Pigeon>

    @Query("SELECT COUNT(*) from pigeon_table")
    fun countPigeons(): Int
}

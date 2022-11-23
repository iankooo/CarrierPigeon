package com.example.carrier_pigeon.features.pigeonsFlights.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.carrier_pigeon.features.pigeonsFlights.data.Record

@Dao
interface RecordDao {
    @Insert
    suspend fun insert(record: Record)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(records: List<Record>)

    @Delete
    suspend fun delete(record: Record)

    @Delete
    suspend fun deleteAll(records: List<Record>)

    @Query("SELECT * FROM `record_table` r JOIN `pigeon_table` p ON r.pigeonId = p.id WHERE p.isDeleted = 0 ")
    fun fetchAllRecords(): LiveData<List<Record>>
}

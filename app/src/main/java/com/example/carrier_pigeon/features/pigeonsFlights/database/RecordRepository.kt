package com.example.carrier_pigeon.features.pigeonsFlights.database

import androidx.lifecycle.LiveData
import com.example.carrier_pigeon.features.pigeonsFlights.data.Record

class RecordRepository(private val recordDao: RecordDao) {
    val allRecords: LiveData<List<Record>> = recordDao.fetchAllRecords()

    suspend fun insert(record: Record) {
        recordDao.insert(record)
    }

    suspend fun insertAll(records: List<Record>) {
        recordDao.insertAll(records)
    }

    suspend fun delete(record: Record) {
        recordDao.deleteByPigeonId(record.pigeonId)
    }

    suspend fun deleteAll(records: List<Record>) {
        recordDao.deleteAll(records)
    }
}

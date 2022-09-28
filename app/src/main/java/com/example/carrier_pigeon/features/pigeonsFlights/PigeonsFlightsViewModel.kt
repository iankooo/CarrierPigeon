package com.example.carrier_pigeon.features.pigeonsFlights

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.carrier_pigeon.features.pigeons.database.PigeonDatabase
import com.example.carrier_pigeon.features.pigeonsFlights.data.Record
import com.example.carrier_pigeon.features.pigeonsFlights.database.RecordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PigeonsFlightsViewModel @Inject constructor(
    application: Application
) : ViewModel() {

    private val mRecordRepository: RecordRepository
    val allRecords: LiveData<List<Record>>

    init {
        val dao = PigeonDatabase.getInstance(application).recordDao()
        mRecordRepository = RecordRepository(dao)
        allRecords = mRecordRepository.allRecords
    }

    fun insertRecord(record: Record) = viewModelScope.launch {
        mRecordRepository.insert(record)
    }

    fun insertAllRecord(records: List<Record>) = viewModelScope.launch {
        mRecordRepository.insertAll(records)
    }

    fun deleteRecord(record: Record) = viewModelScope.launch {
        mRecordRepository.delete(record)
    }

    fun deleteAllRecords(records: List<Record>) = viewModelScope.launch {
        mRecordRepository.deleteAll(records)
    }

    fun getRecordsList() = mRecordRepository.allRecords
}

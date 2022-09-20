package com.example.carrier_pigeon.features.pigeonsFlights

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.carrier_pigeon.features.pigeonsFlights.data.Record
import com.example.carrier_pigeon.features.pigeonsFlights.database.RecordDatabase
import com.example.carrier_pigeon.features.pigeonsFlights.database.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PigeonsFlightsViewModel @Inject constructor(
    application: Application
) : ViewModel() {

    private val repository: Repository
    val allRecords: LiveData<List<Record>>

    init {
        val dao = RecordDatabase.getInstance(application).recordDao()
        repository = Repository(dao)
        allRecords = repository.allRecords
    }

    fun insertRecord(record: Record) = viewModelScope.launch {
        repository.insert(record)
    }

    fun insertAllRecord(records: List<Record>) = viewModelScope.launch {
        repository.insertAll(records)
    }

    fun deleteRecord(record: Record) = viewModelScope.launch {
        repository.delete(record)
    }

    fun deleteAllRecords(records: List<Record>) = viewModelScope.launch {
        repository.deleteAll(records)
    }

    fun getRecordsList() = repository.allRecords
}

package com.example.carrier_pigeon.features.pigeons

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.carrier_pigeon.features.pigeons.data.Pigeon
import com.example.carrier_pigeon.features.pigeons.database.PigeonDatabase
import com.example.carrier_pigeon.features.pigeons.database.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PigeonViewModel @Inject constructor(
    application: Application
) : ViewModel() {

    private val repository: Repository
    val allPigeons: LiveData<List<Pigeon>>

    init {
        val dao = PigeonDatabase.getInstance(application).pigeonDao()
        repository = Repository(dao)
        allPigeons = repository.allPigeons
    }

    fun insert(pigeon: Pigeon) = viewModelScope.launch {
        repository.insert(pigeon)
    }

    fun update(pigeon: Pigeon) = viewModelScope.launch {
        repository.update(pigeon)
    }

    fun delete(pigeon: Pigeon) = viewModelScope.launch {
        repository.delete(pigeon)
    }

    fun getPigeonsList() = repository.allPigeons
}

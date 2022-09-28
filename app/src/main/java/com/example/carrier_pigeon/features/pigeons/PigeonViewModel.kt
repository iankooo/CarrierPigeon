package com.example.carrier_pigeon.features.pigeons

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.carrier_pigeon.features.pigeons.data.Pigeon
import com.example.carrier_pigeon.features.pigeons.database.PigeonDatabase
import com.example.carrier_pigeon.features.pigeons.database.PigeonRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PigeonViewModel @Inject constructor(
    application: Application
) : ViewModel() {

    private val mPigeonRepository: PigeonRepository
    val allPigeons: LiveData<List<Pigeon>>

    init {
        val dao = PigeonDatabase.getInstance(application).pigeonDao()
        mPigeonRepository = PigeonRepository(dao)
        allPigeons = mPigeonRepository.allPigeons
    }

    fun insert(pigeon: Pigeon) = viewModelScope.launch {
        mPigeonRepository.insert(pigeon)
    }

    fun update(pigeon: Pigeon) = viewModelScope.launch {
        mPigeonRepository.update(pigeon)
    }

    fun delete(pigeon: Pigeon) = viewModelScope.launch {
        mPigeonRepository.delete(pigeon)
    }

    fun getPigeonsList() = mPigeonRepository.allPigeons
}

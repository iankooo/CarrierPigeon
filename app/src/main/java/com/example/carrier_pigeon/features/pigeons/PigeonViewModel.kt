package com.example.carrier_pigeon.features.pigeons

import android.app.Application
import androidx.lifecycle.ViewModel
import com.example.carrier_pigeon.features.pigeons.data.Pigeon
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PigeonViewModel @Inject constructor(application: Application) : ViewModel() {
    val dao = PigeonDatabase.getInstance(application).pigeonDao()

    private suspend fun addPigeon(pigeon: Pigeon): Pigeon {
        dao.insert(pigeon)
        return pigeon
    }

    suspend fun deletePigeon(pigeon: Pigeon) {
        return dao.delete(pigeon)
    }

    suspend fun populateWithTestData() {
    }

    fun countUsers() = dao.countPigeons()

    fun getPigeonsList() = dao.fetchAllPigeons()
}

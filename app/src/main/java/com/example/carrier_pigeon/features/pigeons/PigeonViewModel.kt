package com.example.carrier_pigeon.features.pigeons

import android.app.Application
import androidx.lifecycle.ViewModel
import com.example.carrier_pigeon.features.pigeons.data.Pigeon
import com.example.carrier_pigeon.features.pigeons.database.PigeonDatabase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PigeonViewModel @Inject constructor(
    application: Application
) : ViewModel() {
    val dao = PigeonDatabase.getInstance(application).pigeonDao()

    suspend fun deletePigeon(pigeon: Pigeon) {
        return dao.delete(pigeon)
    }

    fun getPigeonsList() = dao.fetchAllPigeons()
}

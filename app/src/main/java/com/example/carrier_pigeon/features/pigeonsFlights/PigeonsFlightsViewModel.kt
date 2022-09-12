package com.example.carrier_pigeon.features.pigeonsFlights

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.carrier_pigeon.features.pigeons.PigeonDatabase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PigeonsFlightsViewModel @Inject constructor(application: Application) :
    AndroidViewModel(application) {
    val dao = PigeonDatabase.getInstance(application).pigeonDao()

    fun getPigeonsList() = dao.fetchAllPigeons()
}

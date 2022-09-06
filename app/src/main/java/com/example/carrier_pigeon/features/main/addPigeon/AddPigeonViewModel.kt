package com.example.carrier_pigeon.features.main.addPigeon

import android.app.Application
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import com.example.carrier_pigeon.features.main.PigeonDatabase
import com.example.carrier_pigeon.features.main.data.Pigeon
import javax.inject.Inject

@HiltViewModel
class AddPigeonViewModel @Inject constructor(application: Application) : ViewModel() {
    var havePermissionsBeenPreviouslyDenied = false
    val dao = PigeonDatabase.getInstance(application).pigeonDao()

    suspend fun addPigeon(pigeon: Pigeon): Pigeon {
        dao.insert(pigeon)
        return pigeon
    }
}

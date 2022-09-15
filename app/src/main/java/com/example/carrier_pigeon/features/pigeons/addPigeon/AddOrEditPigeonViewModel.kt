package com.example.carrier_pigeon.features.pigeons.addPigeon

import android.app.Application
import androidx.lifecycle.ViewModel
import com.example.carrier_pigeon.features.pigeons.database.PigeonDatabase
import com.example.carrier_pigeon.features.pigeons.data.Pigeon
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AddOrEditPigeonViewModel @Inject constructor(application: Application) : ViewModel() {
    var havePermissionsBeenPreviouslyDenied = false
    val dao = PigeonDatabase.getInstance(application).pigeonDao()

    suspend fun addPigeon(pigeon: Pigeon) {
        dao.insert(pigeon)
    }

    suspend fun editPigeon(pigeon: Pigeon) {
        dao.update(pigeon)
    }
}

package com.example.carrier_pigeon.features.main

import android.app.Application
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import com.example.carrier_pigeon.features.main.data.Pigeon
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
        var pigeon = Pigeon(
            "19-372240",
            "F",
            "RO",
            "SPERANTA",
            "GUT",
            "Loc 1 Int Barcelona -Tata Nep Messie -Loc 1 la Nat Spania"
        )
        addPigeon(pigeon)
        pigeon = Pigeon(
            "19-372241",
            "F",
            "RO",
            "SPERANTA",
            "GUT",
            "Loc 1 Int Barcelona -Tata Nep Messie -Loc 1 la Nat Spania"
        )
        addPigeon(pigeon)
    }

    fun countUsers() = dao.countPigeons()

    fun getPigeonsList() = dao.fetchAllPigeons()
}

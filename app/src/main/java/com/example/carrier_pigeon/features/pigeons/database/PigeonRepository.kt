package com.example.carrier_pigeon.features.pigeons.database

import androidx.lifecycle.LiveData
import com.example.carrier_pigeon.features.pigeons.data.Pigeon

class PigeonRepository(private val pigeonDao: PigeonDao) {
    val allPigeons: LiveData<List<Pigeon>> = pigeonDao.fetchAllPigeons()

    suspend fun update(pigeon: Pigeon) {
        pigeonDao.update(pigeon)
    }

    suspend fun insert(pigeon: Pigeon) {
        pigeonDao.insert(pigeon)
    }

    suspend fun delete(pigeon: Pigeon) {
        pigeonDao.delete(pigeon)
    }
}

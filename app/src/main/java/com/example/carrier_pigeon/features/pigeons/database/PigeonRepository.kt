package com.example.carrier_pigeon.features.pigeons.database

import android.app.Application
import androidx.lifecycle.LiveData
import com.example.carrier_pigeon.features.pigeons.detailPigeon.AncestorDescendant
import com.example.carrier_pigeon.features.pigeons.detailPigeon.AncestorDescendantBundle
import com.example.carrier_pigeon.features.pigeons.detailPigeon.AncestorDescendantDao
import com.example.carrier_pigeon.features.pigeons.data.Pigeon

class PigeonRepository(private val pigeonDao: PigeonDao, application: Application) {
    val allPigeons: LiveData<List<Pigeon>> = pigeonDao.fetchAllPigeons()
    private val mAncestorDescendantDao: AncestorDescendantDao =
        PigeonDatabase.getInstance(application).ancestorDescendantDao()

    suspend fun update(pigeon: Pigeon) {
        pigeonDao.update(pigeon)
    }

    suspend fun insert(pigeon: Pigeon) {
        pigeonDao.insert(pigeon)
    }

    suspend fun delete(pigeon: Pigeon) {
        pigeonDao.delete(pigeon)
    }

    suspend fun insertAncestor(ancestorDescendantBundle: AncestorDescendantBundle) {
        val ancestorId: Int? = ancestorDescendantBundle.newPigeon?.id
        val descendantId: Int? =
            ancestorDescendantBundle.existingPigeon?.id
        val depth: Int = ancestorDescendantBundle.depth

        val ancestorDescendant =
            AncestorDescendant(
                ancestorId = ancestorId,
                descendantId = descendantId,
                depth = depth
            )
        mAncestorDescendantDao.insert(ancestorDescendant)
    }

    fun getAllAncestorDescendantsNotLive(): List<AncestorDescendant?>? {
        return mAncestorDescendantDao.getAllAncestorDescendantRecordsNotLive()
    }

    fun getFamilyMemberById(id: Int): List<Pigeon?> {
        return pigeonDao.fetchPigeonById(id)
    }

    fun getAncestorDescendantsByAncestorId(ancestorId: Int): List<AncestorDescendant?>? {
        return mAncestorDescendantDao.getAllAncestorDescendantRecordsByAncestorIdNotLive(ancestorId)
    }
    fun getAncestorDescendantsByDescendantId(descendantId: Int): List<AncestorDescendant?>? {
        return mAncestorDescendantDao.getAllAncestorDescendantRecordsByDescendantIdNotLive(descendantId)
    }
}

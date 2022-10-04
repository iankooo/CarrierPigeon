package com.example.carrier_pigeon.features.pigeons

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.carrier_pigeon.app.BackgroundThreadPoster
import com.example.carrier_pigeon.features.pigeons.data.Pigeon
import com.example.carrier_pigeon.features.pigeons.database.PigeonDatabase
import com.example.carrier_pigeon.features.pigeons.database.PigeonRepository
import com.example.carrier_pigeon.features.pigeons.detailPigeon.AncestorDescendant
import com.example.carrier_pigeon.features.pigeons.detailPigeon.AncestorDescendantBundle
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.concurrent.Callable
import javax.inject.Inject

@HiltViewModel
class PigeonViewModel @Inject constructor(
    application: Application
) : ViewModel() {

    @Inject
    lateinit var backgroundThreadPoster: BackgroundThreadPoster

    private val mPigeonRepository: PigeonRepository
    val allPigeons: LiveData<List<Pigeon>>

    init {
        val dao = PigeonDatabase.getInstance(application).pigeonDao()
        mPigeonRepository = PigeonRepository(dao, application)
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

    fun getFamilyMembersByFamilyTreeId(familyTreeId: Int): List<Pigeon> {
        val familyMembers: MutableList<Pigeon> = ArrayList()
        for (familyMember in allPigeons.value.orEmpty()) {
            if (familyMember.familyTreeId == familyTreeId) {
                familyMembers.add(familyMember)
            }
        }
        return familyMembers
    }

    fun insertAncestor(ancestorDescendantBundle: AncestorDescendantBundle) = viewModelScope.launch {
        mPigeonRepository.insertAncestor(ancestorDescendantBundle)
    }

    fun getFamilyMemberByLocalId(id: Int?): Pigeon? {
        for (familyMember in allPigeons.value!!) {
            if (familyMember.id == id) {
                return familyMember
            }
        }
        return null
    }

    fun getFamilyMemberById(id: Int): Pigeon? {
        val callable = Callable { mPigeonRepository.getFamilyMemberById(id)[0] }
        return backgroundThreadPoster.submit(callable)
    }

    fun getRoots(familyTreeId: Int): List<Pigeon> {
        /*
        TODO: find a way to find all roots in the AncestorDescendant table... rn the function
            assumes that there is only one bloodline modeled in the tree, but there could be
            more than one that do not have a common ancestor
         */
        var ancestorDescendants: List<AncestorDescendant?>? =
            backgroundThreadPoster.submit { mPigeonRepository.getAllAncestorDescendantsNotLive() }
        val temp: MutableList<AncestorDescendant> = ArrayList()
        if (ancestorDescendants != null) {
            for (ancestorDescendant in ancestorDescendants) {
                val ancestorId: Int? = ancestorDescendant?.ancestorId
                val ancestor: Pigeon? = getFamilyMemberByLocalId(ancestorId)
                val ancestorFamilyTreeId: Int? = ancestor?.familyTreeId
                if (familyTreeId == ancestorFamilyTreeId) {
                    temp.add(ancestorDescendant!!)
                }
            }
        }
        ancestorDescendants = temp
        val roots: MutableList<Pigeon> = java.util.ArrayList<Pigeon>()
        if (ancestorDescendants.isNotEmpty()) {
            var root = ancestorDescendants[0]
            for (ancestorDescendant in ancestorDescendants) {
                if (ancestorDescendant.descendantId === root.ancestorId) {
                    root = ancestorDescendant
                    root = reiterateAncestorDescendants(root, ancestorDescendants)
                }
            }
            val familyMember: Pigeon = getFamilyMemberById(root.ancestorId!!)!!
            if (familyMember.familyTreeId == familyTreeId) roots.add(familyMember)
            val familyMembers: List<Pigeon> = getFamilyMembersByFamilyTreeId(familyTreeId)
            for (fm in familyMembers) {
                var familyMemberIsARoot = true
                for (ancestorDescendant in ancestorDescendants) {
                    val familyMemberId: Int = fm.id
                    val ancestorId: Int? = ancestorDescendant.ancestorId
                    val descendantId: Int? = ancestorDescendant.descendantId
                    if (familyMemberId == ancestorId || familyMemberId == descendantId) {
                        familyMemberIsARoot = false
                        break
                    }
                }

                // if the family member has no relationships, then make them a root
                if (familyMemberIsARoot) {
                    roots.add(fm)
                }
            }
        } else {
            // if there are no relationships, then make every family member a root
            roots.addAll(getFamilyMembersByFamilyTreeId(familyTreeId))
        }
        return roots
    }

    private fun reiterateAncestorDescendants(
        root: AncestorDescendant,
        ancestorDescendants: List<AncestorDescendant>
    ): AncestorDescendant {
        var root1 = root
        for (ancestorDescendant in ancestorDescendants) {
            if (ancestorDescendant.descendantId === root1.ancestorId) {
                root1 = ancestorDescendant
            }
        }
        return root1
    }

    fun makeFamilyTreeFor(familyMember: Pigeon) = viewModelScope.launch {
        val familyMembers: List<Pigeon> = makeFamilyTree(familyMember)
        familyMember.children = familyMembers
    }

    private fun makeFamilyTree(root: Pigeon): List<Pigeon> {
        val ancestorDescendants: List<AncestorDescendant?>? =
            backgroundThreadPoster.submit {
                mPigeonRepository.getAncestorDescendantsByDescendantId(
                    root.id
                )
            }
        return if (ancestorDescendants == null) {
            ArrayList()
        } else {
            val parents: MutableList<Pigeon> = ArrayList()
            for (ancestorDescendant in ancestorDescendants) {
                val parent: Pigeon? = getFamilyMemberById(ancestorDescendant?.ancestorId!!)

                // recurse
                val grandparents: List<Pigeon> = makeFamilyTree(parent!!)
                parent.children = grandparents
                parents.add(parent)
            }
            parents
        }
    }
}

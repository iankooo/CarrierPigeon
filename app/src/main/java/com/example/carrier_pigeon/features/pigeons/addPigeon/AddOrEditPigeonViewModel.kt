package com.example.carrier_pigeon.features.pigeons.addPigeon

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AddOrEditPigeonViewModel @Inject constructor() : ViewModel() {
    var havePermissionsBeenPreviouslyDenied = false
}

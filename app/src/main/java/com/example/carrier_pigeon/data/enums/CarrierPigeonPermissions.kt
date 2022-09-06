package com.example.carrier_pigeon.data.enums

import android.Manifest

enum class CarrierPigeonPermissions(val type: String) {
    CAMERA(Manifest.permission.CAMERA),
    READ_EXTERNAL_STORAGE(Manifest.permission.READ_EXTERNAL_STORAGE);

    companion object {
        fun getPermissions(): Array<String> {
            return arrayOf(CAMERA.type, READ_EXTERNAL_STORAGE.type)
        }
    }
}

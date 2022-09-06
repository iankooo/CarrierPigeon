package com.example.carrier_pigeon.app.utils.permissions

interface PermissionsDialogButtonsListener {
    fun onAllowClicked(permissions: Array<String>)
    fun onDenyClicked(permissions: Array<String>)
}

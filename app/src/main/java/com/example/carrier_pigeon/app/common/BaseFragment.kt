package com.example.carrier_pigeon.app.common

import androidx.annotation.LayoutRes
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.carrier_pigeon.R
import com.example.carrier_pigeon.app.utils.permissions.PermissionsDialogButtonsListener

/**
 * Base class for fragments
 */
open class BaseFragment(@LayoutRes layoutId: Int) :
    Fragment(layoutId),
    PermissionsDialogButtonsListener {

    // Function to show the permissions rationale when needed
    protected fun showRationaleDialog(permissions: Array<String>, title: String, message: String) {
        AlertDialog.Builder(requireActivity()).apply {
            setTitle(title)
            setMessage(message)
            setPositiveButton(R.string.permission_rationale_dialog_positive_button_text) { _, _ ->
                onAllowClicked(permissions)
            }
            setNegativeButton(R.string.permission_rationale_dialog_negative_button_text) { dialog, _ ->
                dialog.dismiss()
                onDenyClicked(permissions)
            }
        }.run {
            create()
            show()
        }
    }

    override fun onAllowClicked(permissions: Array<String>) {}

    override fun onDenyClicked(permissions: Array<String>) {}
}

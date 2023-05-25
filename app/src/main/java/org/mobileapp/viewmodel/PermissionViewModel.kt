package org.mobileapp.viewmodel

import android.Manifest
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel

class PermissionViewModel : ViewModel() {
    val necessaryPermissions: MutableMap<String, Boolean> = mutableMapOf(
        Manifest.permission.ACCESS_FINE_LOCATION to false,
        Manifest.permission.ACCESS_COARSE_LOCATION to false,
        Manifest.permission.CAMERA to false,
    )

    val visiblePermissionDialogQueue = mutableStateListOf<String>()

    fun dismissDialog() {
        visiblePermissionDialogQueue.removeFirst()
    }

    fun onPermissionResult(
        permission: String, isGranted: Boolean
    ) {
        necessaryPermissions[permission] = isGranted

        if (!isGranted && !visiblePermissionDialogQueue.contains(permission)) {
            visiblePermissionDialogQueue.add(permission)
        }
    }
}
package org.mobileapp.ui.permission

import android.Manifest
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.LocationManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import org.mobileapp.ui.permission.components.CameraPermissionTextProvider
import org.mobileapp.ui.permission.components.LocationPermissionTextProvider
import org.mobileapp.ui.permission.components.PermissionDialog
import org.mobileapp.viewmodel.PermissionViewModel

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionsView(
    viewModel: PermissionViewModel = viewModel(), navigateToLoginScreen: () -> Unit
) {
    val context = LocalContext.current
    val dialogQueue = viewModel.visiblePermissionDialogQueue
    val multiplePermissionsState =
        rememberMultiplePermissionsState(permissions = viewModel.necessaryPermissions.keys.toList())
    val isLocationEnabled = remember { mutableStateOf(false) }

    when {
        multiplePermissionsState.allPermissionsGranted -> {
            if(isLocationEnabled.value)
                navigateToLoginScreen.invoke()
            else
                Text(text = "Enable localization!")
        }
    }

    val permissionResultLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { perms ->
            viewModel.necessaryPermissions.keys.forEach { permission ->
                viewModel.onPermissionResult(
                    permission = permission, isGranted = perms[permission] == true
                )
            }
        })

    dialogQueue.reversed().forEach { permission ->
        PermissionDialog(permissionTextProvider = when (permission) {
            Manifest.permission.CAMERA -> {
                CameraPermissionTextProvider()
            }
            Manifest.permission.ACCESS_FINE_LOCATION -> {
                LocationPermissionTextProvider()
            }
            Manifest.permission.ACCESS_COARSE_LOCATION -> {
                LocationPermissionTextProvider()
            }
            else -> return@forEach
        }, isPermanentlyDeclined = !shouldShowRequestPermissionRationale(
            LocalContext.current as Activity, permission
        ), onDismiss = viewModel::dismissDialog, onOkClick = {
            viewModel.dismissDialog()
            permissionResultLauncher.launch(
                arrayOf(permission)
            )
        }, onGoToAppSettingsClick = {
            context.startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", context.packageName, null)
            })
        })
    }

    LaunchedEffect(Unit) {
        permissionResultLauncher.launch(viewModel.necessaryPermissions.keys.toTypedArray())
    }

    LaunchedEffect(Unit) {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        isLocationEnabled.value = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

        val locationProviderChangedReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent?.action == LocationManager.PROVIDERS_CHANGED_ACTION) {
                    isLocationEnabled.value = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                }
            }
        }

        val intentFilter = IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION)
        context.registerReceiver(locationProviderChangedReceiver, intentFilter)
    }

}
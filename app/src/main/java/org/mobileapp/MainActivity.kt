package org.mobileapp

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.navigation.NavHostController
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import dagger.hilt.android.AndroidEntryPoint
import org.mobileapp.data.configuration.TrackerServiceConfig
import org.mobileapp.navigation.NavGraph
import org.mobileapp.navigation.Screen
import org.mobileapp.service.TrackerService
import org.mobileapp.service.enums.ServiceAction
import org.mobileapp.viewmodel.LoginViewModel
import org.mobileapp.viewmodel.MapViewModel
import org.osmdroid.config.Configuration

@ExperimentalFoundationApi
@AndroidEntryPoint
@ExperimentalAnimationApi
class MainActivity : ComponentActivity() {
    private lateinit var navController: NavHostController
    private val loginViewModel by viewModels<LoginViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set User Agent To Prevent Getting Banned From The OSM Servers
        Configuration.getInstance().userAgentValue = BuildConfig.APPLICATION_ID
        // Set The Path For OSM's Files
        Configuration.getInstance().osmdroidBasePath = this.getExternalFilesDir(null)

        setContent {
            navController = rememberAnimatedNavController()
            NavGraph(navController = navController)


           // if (loginViewModel.isUserAuthenticated) {
           //     navController.navigate(Screen.ProfileScreen.route)
           // }
        }

        sendActionCommandToService(ServiceAction.START_SERVICE.str)
    }

    override fun onDestroy() {
        super.onDestroy()

        sendActionCommandToService(ServiceAction.STOP_SERVICE.str)
    }

    private fun sendActionCommandToService(action: String) {
        Intent(applicationContext, TrackerService::class.java).also {
            it.action = action
            applicationContext.startService(it)
        }
    }
}
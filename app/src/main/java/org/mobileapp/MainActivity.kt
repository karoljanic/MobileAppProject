package org.mobileapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.MaterialTheme
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import org.mobileapp.navigation.NavGraph
import org.mobileapp.service.TrackerService
import org.mobileapp.service.enums.ServiceAction
import org.mobileapp.viewmodel.LoginViewModel
import org.osmdroid.config.Configuration

@ExperimentalFoundationApi
@AndroidEntryPoint
@ExperimentalAnimationApi
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set User Agent To Prevent Getting Banned From The OSM Servers
        Configuration.getInstance().userAgentValue = BuildConfig.APPLICATION_ID
        // Set The Path For OSM's Files
        Configuration.getInstance().osmdroidBasePath = this.getExternalFilesDir(null)

        setContent {
            MaterialTheme {
                NavGraph()
            }
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
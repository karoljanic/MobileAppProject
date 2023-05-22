package org.mobileapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.navigation.NavHostController
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import dagger.hilt.android.AndroidEntryPoint
import org.mobileapp.navigation.NavGraph
import org.mobileapp.navigation.Screen
import org.mobileapp.settings.Settings.initPreferences
import org.mobileapp.viewmodel.LoginViewModel
import org.osmdroid.config.Configuration

@AndroidEntryPoint
@ExperimentalAnimationApi
class MainActivity : ComponentActivity() {
    private lateinit var navController: NavHostController
    private val viewModel by viewModels<LoginViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize SharedPreferences
        initPreferences()

        // Set User Agent To Prevent Getting Banned From The OSM Servers
        Configuration.getInstance().userAgentValue = BuildConfig.APPLICATION_ID
        // Set The Path For OSM's Files
        Configuration.getInstance().osmdroidBasePath = this.getExternalFilesDir(null)

        setContent {
            navController = rememberAnimatedNavController()
            NavGraph(
                navController = navController
            )

            if (viewModel.isUserAuthenticated) {
                navController.navigate(Screen.ProfileScreen.route)
            }
        }

    }
}
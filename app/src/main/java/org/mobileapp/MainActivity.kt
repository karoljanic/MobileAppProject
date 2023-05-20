package org.mobileapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.mobileapp.settings.Settings.initPreferences
import org.mobileapp.ui.MainView
import org.mobileapp.ui.theme.MobileAppTheme
import org.osmdroid.config.Configuration

class MainActivity : ComponentActivity() {
    //private val mainViewModel : ViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initPreferences()

        Configuration.getInstance().userAgentValue = BuildConfig.APPLICATION_ID

        // set the path for osmdroid's files
        Configuration.getInstance().osmdroidBasePath = this.getExternalFilesDir(null)

        setContent {
            MobileAppTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
                    MainView()
                }
            }
        }
    }
}
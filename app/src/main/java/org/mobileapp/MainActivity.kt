package org.mobileapp

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.osmdroid.config.Configuration

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // set user agent to prevent getting banned from the osm servers
        Configuration.getInstance().userAgentValue = BuildConfig.APPLICATION_ID
        // set the path for osmdroid's files (e.g. tile cache)
        Configuration.getInstance().osmdroidBasePath = this.getExternalFilesDir(null)

        // set up views
        setContentView(R.layout.activity_main)
        //navHostFragment  = findViewById<BottomNavigationView>(R.id.main_container)
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.navigation_view)
        val navController = findNavController(R.id.fragment_view)
        bottomNavigationView.setupWithNavController(navController = navController)

        /*
        // listen for navigation changes
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.track_fragment -> {
                    runOnUiThread {
                        run {
                            // mark menu item "Tracks" as checked
                            bottomNavigationView.menu.findItem(R.id.tracklist_fragment).isChecked =
                                true
                        }
                    }
                }
                else -> {
                    // do nothing
                }
            }
        }
         */

        //PreferencesHelper.registerPreferenceChangeListener(sharedPreferenceChangeListener)
    }


    override fun onDestroy() {
        super.onDestroy()
        //PreferencesHelper.unregisterPreferenceChangeListener(sharedPreferenceChangeListener)
    }
    /*
    private val sharedPreferenceChangeListener =
        SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
            when (key) {
                Keys.PREF_THEME_SELECTION -> {
                    AppThemeHelper.setTheme(PreferencesHelper.loadThemeSelection())
                }
            }
        }

     */
}
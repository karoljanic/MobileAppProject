package org.mobileapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.mobileapp.config.MapConfig.initPreferences
import org.osmdroid.config.Configuration

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initPreferences()

        // set user agent to prevent getting banned from the osm servers
        Configuration.getInstance().userAgentValue = BuildConfig.APPLICATION_ID

        // set the path for osmdroid's files
        Configuration.getInstance().osmdroidBasePath = this.getExternalFilesDir(null)

        setContentView(R.layout.activity_main)

        val navigationView = findViewById<BottomNavigationView>(R.id.navigation_view)
        val navigationController = findNavController(R.id.fragment_view)

        navigationView.setupWithNavController(navController = navigationController)
    }
}
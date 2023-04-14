package org.mobileapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MapFragment : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.map_fragment)
    }
}
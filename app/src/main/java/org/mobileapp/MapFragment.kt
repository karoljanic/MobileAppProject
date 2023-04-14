package org.mobileapp

import android.Manifest
import android.content.*
import android.content.pm.PackageManager
import android.os.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import org.mobileapp.containers.MapLayoutContainer
import org.mobileapp.config.MapConfig
import org.osmdroid.util.GeoPoint

class MapFragment : Fragment() {
    private lateinit var layout: MapLayoutContainer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        layout = MapLayoutContainer(activity as Context, container, inflater)
        layout.myLocation.setOnClickListener {
            layout.setLocation(MapConfig.getLocation())
        }

        return layout.rootView
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onStop() {
        super.onStop()
    }

}
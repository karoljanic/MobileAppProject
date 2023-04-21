package org.mobileapp

import android.content.*
import android.os.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import org.mobileapp.settings.Settings
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
            layout.setLocation(GeoPoint(Settings.getLastLocation()))
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
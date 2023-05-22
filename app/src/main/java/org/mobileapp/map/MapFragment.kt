package org.mobileapp.map

import android.Manifest
import android.content.*
import android.content.pm.PackageManager
import android.location.Location
import android.os.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import org.mobileapp.settings.Settings
import org.mobileapp.settings.SettingsKeys
import org.mobileapp.tracking.config.Configuration
import org.mobileapp.tracking.enums.ServiceBindStatus
import org.mobileapp.tracking.enums.ServiceStatus
import org.mobileapp.service.LocalTrackerServiceBinder
import org.mobileapp.service.TrackerService
import org.mobileapp.tracking.track.Track
import org.mobileapp.tracking.utils.LocationUtil


class MapFragment : Fragment() {
    private lateinit var layout: MapLayoutContainer

    private lateinit var trackerService: TrackerService
    private var trackingServiceBindStatus: ServiceBindStatus = ServiceBindStatus.IS_NOT_BOUNDED
    private var trackingServiceStatus: ServiceStatus = ServiceStatus.IS_NOT_RUNNING

    private var gpsProviderActive: Boolean = false
    private var networkProviderActive: Boolean = false

    private lateinit var currentBestLocation: Location
    private var track: Track = Track()

    private val handler: Handler = Handler(Looper.getMainLooper())

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            trackingServiceBindStatus = ServiceBindStatus.IS_BOUNDED

            val binder = service as LocalTrackerServiceBinder
            trackerService = binder.trackerService

            trackingServiceStatus = trackerService.getStatus()

            handler.removeCallbacks(periodicLocationRequestRunnable)
            handler.postDelayed(periodicLocationRequestRunnable, 0)

            startTracking()
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            handleServiceUnbind()
        }
    }

    private val requestLocationPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        if (isGranted) {
            activity?.unbindService(connection)
            activity?.bindService(Intent(activity, TrackerService::class.java), connection, Context.BIND_AUTO_CREATE)
        } else {
            activity?.unbindService(connection)
        }
    }

    private val periodicLocationRequestRunnable: Runnable = object : Runnable {
        override fun run() {
            currentBestLocation = trackerService.getCurrentLocation()
            track = trackerService.getTrack()
            gpsProviderActive = trackerService.isGpsProviderActive()
            networkProviderActive = trackerService.isNetworkProviderActive()
            trackingServiceStatus = trackerService.getStatus()

            layout.markCurrentTrack(track)
            layout.markCurrentPosition(currentBestLocation)

            handler.postDelayed(this, Configuration.TIME_BETWEEN_CURRENT_LOCATION_REQUESTS)
        }
    }

    private val startTrackingPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
        startTrackerService()
        trackerService.startTracking()
    }

    private val resumeTrackingPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
        startTrackerService()
        trackerService.resumeTracking()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        currentBestLocation = LocationUtil.getLastKnownLocation(activity as Context)
        trackingServiceStatus = Settings.getCurrentServiceStatus()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        layout = MapLayoutContainer(activity as Context, container, inflater)
        layout.myCurrentLocation.setOnClickListener {
            layout.setCurrentLocation(currentBestLocation, animated = true)
        }

        return layout.rootView
    }

    override fun onStart() {
        super.onStart()

        if (ContextCompat.checkSelfPermission(activity as Context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            requestLocationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        activity?.bindService(Intent(activity, TrackerService::class.java), connection, Context.BIND_AUTO_CREATE)
    }

    override fun onResume() {
        super.onResume()

        try { trackerService.resumeTracking() } catch (_: Exception) { }
    }

    override fun onPause() {
        super.onPause()

        if (trackingServiceBindStatus == ServiceBindStatus.IS_BOUNDED && trackingServiceStatus != ServiceStatus.IS_RUNNING) {
            trackerService.removeGpsLocationListener()
            trackerService.removeNetworkLocationListener()
        }

        trackerService.stopTracking()
    }

    override fun onStop() {
        super.onStop()

        activity?.unbindService(connection)
        handleServiceUnbind()
    }

    private fun startTracking() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && ContextCompat.checkSelfPermission(
                activity as Context,
                Manifest.permission.ACTIVITY_RECOGNITION
            ) == PackageManager.PERMISSION_DENIED
        ) {
            startTrackingPermissionLauncher.launch(Manifest.permission.ACTIVITY_RECOGNITION)
        } else {
            startTrackerService()
            trackerService.startTracking()
        }
    }

    private fun resumeTracking() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && ContextCompat.checkSelfPermission(
                activity as Context,
                Manifest.permission.ACTIVITY_RECOGNITION
            ) == PackageManager.PERMISSION_DENIED
        ) {
            resumeTrackingPermissionLauncher.launch(Manifest.permission.ACTIVITY_RECOGNITION)
        } else {
            startTrackerService()
            trackerService.resumeTracking()
        }
    }

    private fun startTrackerService() {
        val intent = Intent(activity, TrackerService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // ... start service in foreground to prevent it being killed on Oreo
            activity?.startForegroundService(intent)
        } else {
            activity?.startService(intent)
        }
    }

    private fun handleServiceUnbind() {
        trackingServiceBindStatus = ServiceBindStatus.IS_NOT_BOUNDED
        Settings.unregisterPreferenceChangeListener(sharedPreferenceChangeListener)
        handler.removeCallbacks(periodicLocationRequestRunnable)
    }

    private fun handleTrackingManagementMenu() {
        when (trackingServiceStatus) {
            ServiceStatus.IS_PAUSED -> resumeTracking()
            ServiceStatus.IS_RUNNING -> trackerService.stopTracking()
            ServiceStatus.IS_NOT_RUNNING -> startTracking()
        }
    }

    private val sharedPreferenceChangeListener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
        when (key) {
            SettingsKeys.CURRENT_SERVICE_STATUS -> {
                if (activity != null) {
                    trackingServiceStatus = Settings.getCurrentServiceStatus()
                }
            }
        }
    }
}
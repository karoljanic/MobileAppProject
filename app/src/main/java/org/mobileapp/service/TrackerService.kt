package org.mobileapp.service

import android.Manifest
import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.*
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.mobileapp.data.configuration.TrackerServiceConfig
import org.mobileapp.data.datastore.MapSettings
import org.mobileapp.domain.model.Track
import org.mobileapp.notifications.TrackingNotificationBuilder
import org.mobileapp.service.enums.ServiceAction.*
import org.mobileapp.service.enums.ServiceStatus
import org.mobileapp.utils.LocationUtils
import java.util.*

@AndroidEntryPoint
class TrackerService : LifecycleService() {
    private lateinit var locationManager: LocationManager
    private lateinit var notificationManager: NotificationManager

    private lateinit var gpsLocationListener: LocationListener
    private lateinit var networkLocationListener: LocationListener
    private lateinit var trackingNotificationBuilder: TrackingNotificationBuilder

    private var gpsProviderActive: Boolean = false
    private var networkProviderActive: Boolean = false

    private var gpsLocationListenerRegistered: Boolean = false
    private var networkLocationListenerRegistered: Boolean = false

    private var serviceStatus = ServiceStatus.IS_NOT_RUNNING

    private val handler: Handler = Handler(Looper.getMainLooper())

    companion object {
        val isTracking = MutableLiveData<Boolean>()
        val track = MutableLiveData<Track>()
        val trackingTime = MutableLiveData<Long>()
        val trackingDistance = MutableLiveData<Double>()
        val currentLocation = MutableLiveData<Location>()
    }

    override fun onCreate() {
        super.onCreate()

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        gpsLocationListener = createLocationListener()
        networkLocationListener = createLocationListener()
        trackingNotificationBuilder = TrackingNotificationBuilder(this)

        gpsProviderActive = LocationUtils.isGpsEnabled(locationManager)
        networkProviderActive = LocationUtils.isNetworkEnabled(locationManager)

        Log.i("XLOC", "GPS1 = $gpsProviderActive")
        Log.i("XLOC", "GPS2 = $networkProviderActive")

        runBlocking { currentLocation.postValue(MapSettings(applicationContext).getLocation.first()!!) }

        restartStates()

        isTracking.observe(this) {
            updateLocationTracking(it)
            updateNotificationTrackingState(it)
        }
    }

    private fun restartStates() {
        isTracking.postValue(false)
        track.postValue(Track())
        trackingTime.postValue(0)
        trackingTime.postValue(0)
    }

    private fun updateLocationTracking(isTracking: Boolean) {
    }

    private fun updateNotificationTrackingState(isTracking: Boolean?) {

    }

    private fun startTracking() {
        addGpsLocationListener()
        addNetworkLocationListener()

        restartStates()

        handler.postDelayed(periodicTrackUpdate, 0)
        startForeground(TrackerServiceConfig.NOTIFICATION_ID, displayNotification())
    }

    private fun pauseService() {
        isTracking.postValue(false)
    }

    private fun resumeService() {
        isTracking.postValue(true)
    }

    private fun stopService() {
        isTracking.postValue(false)
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()

        handler.removeCallbacks(periodicTrackUpdate)

        removeGpsLocationListener()
        removeNetworkLocationListener()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when (it.action) {
                START_SERVICE.str -> {
                    startTracking()
                }
                PAUSE_SERVICE.str -> {
                    pauseService()
                }
                RESUME_SERVICE.str -> {
                    resumeService()
                }
                STOP_SERVICE.str -> {
                    stopService()
                }
            }
        }

        return super.onStartCommand(intent, flags, startId)
    }

    /*
    fun clearTrack() {
        track = Track()
        LocalDataUtils.deleteTempFile(this)
        serviceStatus = ServiceAction.IS_NOT_RUNNING

        Settings.setCurrentServiceStatus(serviceStatus)
        stopForeground(STOP_FOREGROUND_DETACH)
        notificationManager.cancel(TrackerServiceConfig.TRACKER_SERVICE_NOTIFICATION_ID)

        Log.i("CLEAR", "TRACK")
    }
     */


    private fun createLocationListener(): LocationListener {
        return object : LocationListener {
            override fun onLocationChanged(location: Location) {
                if (LocationUtils.newLocationIsReliable(location, currentLocation.value)) {
                    currentLocation.postValue(location)
                    Log.i("XLOC", "NEW VALUE ADDED")
                }
            }

            override fun onProviderEnabled(provider: String) {
                when (provider) {
                    LocationManager.GPS_PROVIDER -> gpsProviderActive = LocationUtils.isGpsEnabled(
                        locationManager
                    )

                    LocationManager.NETWORK_PROVIDER -> networkProviderActive =
                        LocationUtils.isNetworkEnabled(
                            locationManager
                        )
                }
            }

            override fun onProviderDisabled(provider: String) {
                when (provider) {
                    LocationManager.GPS_PROVIDER -> gpsProviderActive = LocationUtils.isGpsEnabled(
                        locationManager
                    )

                    LocationManager.NETWORK_PROVIDER -> networkProviderActive =
                        LocationUtils.isNetworkEnabled(
                            locationManager
                        )
                }
            }
        }
    }


    private fun addGpsLocationListener() {
        if (!gpsLocationListenerRegistered) {
            gpsProviderActive = LocationUtils.isGpsEnabled(locationManager)
            if (gpsProviderActive) {

                if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        0,
                        0.0F,
                        gpsLocationListener
                    )
                    gpsLocationListenerRegistered = true
                }
            }
        }
    }


    private fun addNetworkLocationListener() {
        if (!networkLocationListenerRegistered) {
            networkProviderActive = LocationUtils.isNetworkEnabled(locationManager)
            if (networkProviderActive) {
                if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    locationManager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        0,
                        0.0F,
                        networkLocationListener
                    )
                    networkLocationListenerRegistered = true
                }
            }
        }
    }


    private fun removeGpsLocationListener() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            locationManager.removeUpdates(gpsLocationListener)
            gpsLocationListenerRegistered = false
        }
    }


    private fun removeNetworkLocationListener() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            locationManager.removeUpdates(networkLocationListener)
            networkLocationListenerRegistered = false
        }
    }

    private fun displayNotification(): Notification {
        val notification: Notification = trackingNotificationBuilder.build(
            serviceStatus,
            1000
        )

        notificationManager.notify(
            TrackerServiceConfig.NOTIFICATION_ID,
            notification
        )

        return notification
    }

    private fun addNodeToTrack(): Boolean {
        //Log.i("AKTUALNA POZYCJA: ", currentLocation.value.toString())
        /*
        val previousLocation: Location?
        var numberOfNodes: Int = track.trackNodes.size

        when (numberOfNodes) {
            0 -> {
                previousLocation = null
            }
            1 -> {
                previousLocation = null
                numberOfNodes = 0
                track.trackNodes.removeAt(0)
            }
            else -> {
                previousLocation = track.trackNodes[numberOfNodes - 1].getLocation()
            }
        }

        val shouldBeAdded: Boolean = LocationUtils.isRecentEnough(lastLocation) &&
                LocationUtils.isAccurateEnough(
                    lastLocation,
                    TrackerServiceConfig.LOCATION_ACCURACY_THRESHOLD
                ) &&
                LocationUtils.isDifferentEnough(
                    previousLocation,
                    lastLocation,
                    TrackerServiceConfig.ACCURACY_MULTIPLIER
                )

        if (shouldBeAdded) {
            track.trackNodes.add(TrackNode(lastLocation))

            return true
        }

         */

        return false
    }

    private val periodicTrackUpdate: Runnable = object : Runnable {
        override fun run() {
            val successfullyAdded = addNodeToTrack()

            displayNotification()

            handler.postDelayed(
                this,
                TrackerServiceConfig.TIME_BETWEEN_WRITING_OF_SUCCESSIVE_TRACK_NODES
            )
        }
    }
}
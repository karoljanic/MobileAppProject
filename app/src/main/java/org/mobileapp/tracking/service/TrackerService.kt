package org.mobileapp.tracking.service

import android.Manifest
import android.app.Notification
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.*
import android.util.Log
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.mobileapp.notifications.TrackingNotificationBuilder
import org.mobileapp.settings.Settings
import org.mobileapp.tracking.config.Configuration
import org.mobileapp.tracking.enums.ServiceAction
import org.mobileapp.tracking.enums.ServiceBindStatus
import org.mobileapp.tracking.enums.ServiceStatus
import org.mobileapp.tracking.track.Track
import org.mobileapp.tracking.track.TrackNode
import org.mobileapp.tracking.utils.LocalDataUtil
import org.mobileapp.tracking.utils.LocationUtil
import org.mobileapp.tracking.utils.TrackUtil
import java.util.*

class TrackerService : Service(), SensorEventListener {
    private lateinit var locationManager: LocationManager
    private lateinit var sensorManager: SensorManager
    private lateinit var notificationManager: NotificationManager

    private lateinit var gpsLocationListener: LocationListener
    private lateinit var networkLocationListener: LocationListener
    private lateinit var trackingNotificationBuilder: TrackingNotificationBuilder

    private var gpsProviderActive: Boolean = false
    private var networkProviderActive: Boolean = false

    private var gpsLocationListenerRegistered: Boolean = false
    private var networkLocationListenerRegistered: Boolean = false

    private var lastLocation: Location = Settings.getDefaultLocation()
    private var stepCountOffset: Float = 0.0F
    private var lastSave: Date = Date(0L)

    private var serviceBindStatus: ServiceBindStatus = ServiceBindStatus.IS_NOT_BOUNDED
    private var serviceStatus: ServiceStatus = ServiceStatus.IS_NOT_RUNNING
    private var serviceIsResumed: Boolean = false

    private var track: Track = Track()

    private val binder = LocalTrackerServiceBinder(this@TrackerService)
    private val handler: Handler = Handler(Looper.getMainLooper())


    override fun onCreate() {
        super.onCreate()

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        sensorManager = this.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        gpsLocationListener = createLocationListener()
        networkLocationListener = createLocationListener()
        trackingNotificationBuilder = TrackingNotificationBuilder(this)

        gpsProviderActive = LocationUtil.isGpsEnabled(locationManager)
        networkProviderActive = LocationUtil.isNetworkEnabled(locationManager)

        lastLocation = LocationUtil.getLastKnownLocation(this)

        serviceBindStatus = ServiceBindStatus.IS_NOT_BOUNDED
        serviceStatus = ServiceStatus.IS_NOT_RUNNING
        serviceIsResumed = false

        track = TrackUtil.readTrack(this, LocalDataUtil.getTempFileUri(this))
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent == null) {
            if (serviceStatus == ServiceStatus.IS_RUNNING) {
                resumeTracking()
            }
        } else if (ServiceAction.ACTION_STOP.string == intent.action) {
            stopTracking()
        } else if (ServiceAction.ACTION_START.string == intent.action) {
            startTracking(true)
        } else if (ServiceAction.ACTION_RESUME.string == intent.action) {
            resumeTracking()
        }

        return START_STICKY
    }

    override fun onBind(p0: Intent?): IBinder {
        addGpsLocationListener()
        addNetworkLocationListener()

        serviceBindStatus = ServiceBindStatus.IS_BOUNDED

        return binder
    }


    override fun onRebind(intent: Intent?) {
        addGpsLocationListener()
        addNetworkLocationListener()

        serviceBindStatus = ServiceBindStatus.IS_BOUNDED
    }


    override fun onUnbind(intent: Intent?): Boolean {
        if (serviceStatus != ServiceStatus.IS_RUNNING) {
            removeGpsLocationListener()
            removeNetworkLocationListener()
        }

        serviceBindStatus = ServiceBindStatus.IS_NOT_BOUNDED

        return true
    }

    override fun onDestroy() {
        super.onDestroy()

        if (serviceStatus == ServiceStatus.IS_RUNNING)
            stopTracking()

        stopForeground(STOP_FOREGROUND_DETACH)
        notificationManager.cancel(Configuration.TRACKER_SERVICE_NOTIFICATION_ID)

        removeGpsLocationListener()
        removeNetworkLocationListener()
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) { }


    override fun onSensorChanged(sensorEvent: SensorEvent?) {
        var steps: Float = 0.0F

        if (sensorEvent != null) {
            if (stepCountOffset == 0.0F) {
                stepCountOffset = (sensorEvent.values[0] - 1) - track.stepCount
            }

            steps = sensorEvent.values[0] - stepCountOffset
        }

        track.stepCount = steps
    }

    fun clearTrack() {
        track = Track()
        LocalDataUtil.deleteTempFile(this)
        serviceStatus = ServiceStatus.IS_NOT_RUNNING

        Settings.setCurrentServiceStatus(serviceStatus)
        stopForeground(STOP_FOREGROUND_DETACH)
        notificationManager.cancel(Configuration.TRACKER_SERVICE_NOTIFICATION_ID)

        Log.i("CLEAR", "TRACK")
    }

    fun getStatus(): ServiceStatus { return serviceStatus }

    fun getBindStatus(): ServiceBindStatus { return serviceBindStatus }

    fun getCurrentLocation(): Location { return lastLocation }

    fun getTrack(): Track { return track }

    fun isGpsProviderActive(): Boolean { return gpsProviderActive }

    fun isNetworkProviderActive(): Boolean { return networkProviderActive }


    fun resumeTracking() {
        track = TrackUtil.readTrack(this, LocalDataUtil.getTempFileUri(this))
        serviceIsResumed = true

        startTracking(false)
    }

    fun startTracking(newTrack: Boolean = true) {
        addGpsLocationListener()
        addNetworkLocationListener()

        if (newTrack) {
            val now: Date = GregorianCalendar.getInstance().time
            track = Track()

            track.name = now.toString()
            track.startDate = now
            track.endDate = now
            stepCountOffset = 0.0F
        }

        serviceStatus = ServiceStatus.IS_RUNNING
        Settings.setCurrentServiceStatus(serviceStatus)

        startStepCounter()
        handler.postDelayed(periodicTrackUpdate, 0)

        startForeground(Configuration.TRACKER_SERVICE_NOTIFICATION_ID, displayNotification())
    }


    fun stopTracking() {
        CoroutineScope(Dispatchers.IO).launch { TrackUtil.saveTempTrackSuspended(this@TrackerService, track) }

        serviceStatus = ServiceStatus.IS_PAUSED
        Settings.setCurrentServiceStatus(serviceStatus)

        sensorManager.unregisterListener(this)
        handler.removeCallbacks(periodicTrackUpdate)

        displayNotification()
        stopForeground(STOP_FOREGROUND_DETACH)
    }


    private fun createLocationListener(): LocationListener {
        return object : LocationListener {
            override fun onLocationChanged(location: Location) {
                if (LocationUtil.newLocationIsReliable(location, lastLocation)) {
                    lastLocation = location
                }
            }

            override fun onProviderEnabled(provider: String) {
                when (provider) {
                    LocationManager.GPS_PROVIDER -> gpsProviderActive = LocationUtil.isGpsEnabled(
                        locationManager
                    )

                    LocationManager.NETWORK_PROVIDER -> networkProviderActive =
                        LocationUtil.isNetworkEnabled(
                            locationManager
                        )
                }
            }

            override fun onProviderDisabled(provider: String) {
                when (provider) {
                    LocationManager.GPS_PROVIDER -> gpsProviderActive = LocationUtil.isGpsEnabled(
                        locationManager
                    )

                    LocationManager.NETWORK_PROVIDER -> networkProviderActive =
                        LocationUtil.isNetworkEnabled(
                            locationManager
                        )
                }
            }
        }
    }


    private fun addGpsLocationListener() {
        if (!gpsLocationListenerRegistered) {
            gpsProviderActive = LocationUtil.isGpsEnabled(locationManager)
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
            networkProviderActive = LocationUtil.isNetworkEnabled(locationManager)
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


    fun removeGpsLocationListener() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.removeUpdates(gpsLocationListener)
            gpsLocationListenerRegistered = false
        }
    }


    fun removeNetworkLocationListener() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.removeUpdates(networkLocationListener)
            networkLocationListenerRegistered = false
        }
    }


    private fun startStepCounter() {
        val stepCounterAvailable =
            sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER), SensorManager.SENSOR_DELAY_UI)
        if (!stepCounterAvailable) {
            track.stepCount = -1f
        }
    }


    private fun displayNotification(): Notification {
        val notification: Notification = trackingNotificationBuilder.build(
            serviceStatus,
            1000
        )

        notificationManager.notify(Configuration.TRACKER_SERVICE_NOTIFICATION_ID, notification)

        return notification
    }

    private fun addNodeToTrack(): Boolean {
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

        val shouldBeAdded: Boolean = LocationUtil.isRecentEnough(lastLocation) &&
                LocationUtil.isAccurateEnough(lastLocation, Configuration.LOCATION_ACCURACY_THRESHOLD)  &&
                LocationUtil.isDifferentEnough(previousLocation, lastLocation, Configuration.ACCURACY_MULTIPLIER)

        if (shouldBeAdded) {
            track.trackNodes.add(TrackNode(lastLocation))

            return true
        }

        return false
    }

    private val periodicTrackUpdate: Runnable = object : Runnable {
        override fun run() {
            val successfullyAdded = addNodeToTrack()

            if (successfullyAdded) {
                if (serviceIsResumed) {
                    serviceIsResumed = false
                }

                val now: Date = GregorianCalendar.getInstance().time
                if (now.time - lastSave.time > Configuration.TIME_BETWEEN_SAVING_TRACK_TEMPORARY_FILES) {
                    lastSave = now
                    CoroutineScope(Dispatchers.IO).launch { TrackUtil.saveTempTrackSuspended(this@TrackerService, track) }
                }
            }

            displayNotification()

            handler.postDelayed(this, Configuration.TIME_BETWEEN_WRITING_OF_SUCCESSIVE_TRACK_NODES)
        }
    }
}
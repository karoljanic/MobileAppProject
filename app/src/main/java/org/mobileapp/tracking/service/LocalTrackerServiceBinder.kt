package org.mobileapp.tracking.service

import android.os.Binder

class LocalTrackerServiceBinder(val trackerService: TrackerService) : Binder()
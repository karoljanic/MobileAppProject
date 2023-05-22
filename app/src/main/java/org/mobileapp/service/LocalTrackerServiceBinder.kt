package org.mobileapp.service

import android.os.Binder

class LocalTrackerServiceBinder(val trackerService: TrackerService) : Binder()
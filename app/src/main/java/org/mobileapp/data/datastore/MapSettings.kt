package org.mobileapp.data.datastore

import android.content.Context
import android.location.Location
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MapSettings(private val context: Context) {

    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("mapSettings")

        private const val DEFAULT_MAP_ZOOM = 15.0
        private const val DEFAULT_LOCATION_LATITUDE = 51.1078852    // Wroclaw latitude
        private const val DEFAULT_LOCATION_LONGITUDE = 17.0385376   // Wroclaw longitude
        private const val DEFAULT_LOCATION_PROVIDER = ""

        private val MAP_ZOOM = doublePreferencesKey("MAP_ZOOM")
        private val LOCATION_LATITUDE = doublePreferencesKey("LOCATION_LATITUDE")
        private val LOCATION_LONGITUDE = doublePreferencesKey("LOCATION_LONGITUDE")
    }

    val getZoom: Flow<Double> = context.dataStore.data.map { preferences ->
        preferences[MAP_ZOOM] ?: DEFAULT_MAP_ZOOM
    }

    suspend fun saveZoom(zoom: Double) {
        context.dataStore.edit { preferences ->
            preferences[MAP_ZOOM] = zoom
        }
    }

    val getLocation: Flow<Location> = context.dataStore.data.map { preferences ->
        val latitude = preferences[LOCATION_LATITUDE] ?: DEFAULT_LOCATION_LATITUDE
        val longitude = preferences[LOCATION_LONGITUDE] ?: DEFAULT_LOCATION_LONGITUDE

        Location(DEFAULT_LOCATION_PROVIDER).apply {
            this.latitude = latitude
            this.longitude = longitude
        }

    }

    suspend fun saveLocation(location: Location) {
        context.dataStore.edit { preferences ->
            preferences[LOCATION_LATITUDE] = location.latitude
            preferences[LOCATION_LONGITUDE] = location.longitude
        }
    }

}
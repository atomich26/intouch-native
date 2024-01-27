package com.diegusmich.intouch.providers

import android.content.Context
import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.tasks.await

object UserLocationProvider {

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    val defaultLocation = Location("").apply {
        latitude = 41.902782
        longitude = 12.496366
    }

    fun build(context: Context) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    }

    suspend fun getCurrentLocation(): Location {
        return try {
            fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null).await() ?: fusedLocationClient.lastLocation.await() ?: defaultLocation
        } catch (e: SecurityException) {
            defaultLocation
        }
    }
}
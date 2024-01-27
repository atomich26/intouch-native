package com.diegusmich.intouch.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import com.diegusmich.intouch.providers.UserLocationProvider

class LocationBroadcasterReceiver(ctx: Context) : BroadcastReceiver() {

    private val locationManager = ctx.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    private var oldState = isLocationEnabled()

    override fun onReceive(ctx: Context, intent: Intent?) {

    }

    private fun isLocationEnabled() : Boolean{
        return locationManager.let {
            it.isProviderEnabled(LocationManager.GPS_PROVIDER) && it.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        }
    }
}
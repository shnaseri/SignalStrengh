package com.shnaseri.strenghmap.controller

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.util.Log
import androidx.core.app.ActivityCompat

class PositioningManager(private val mContext: Context) {
    private val mLocationManager: LocationManager =
        mContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    init {
        Log.d(TAG, "Position Manager started")
    }

    /**
     * Starts location updates
     */
    fun startLocationUpdates() {
        val providerGps = LocationManager.GPS_PROVIDER
        val providerNetwork = LocationManager.NETWORK_PROVIDER
        // Launch updates from LocationManager
        val pi = getLocationPendingIntent(true)
        if (ActivityCompat.checkSelfPermission(
                mContext,
                Manifest.permission.ACCESS_FINE_LOCATION,
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                mContext,
                Manifest.permission.ACCESS_COARSE_LOCATION,
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        mLocationManager.requestLocationUpdates(providerGps, 0, 15f, pi!!)
        mLocationManager.requestLocationUpdates(providerNetwork, 0, 15f, pi)
        Log.d(TAG, "Location updates started")
    }

    /**
     * Stops location updates
     */
    fun stopLocationUpdates() {
        val pi = getLocationPendingIntent(false)
        if (pi != null) {
            mLocationManager.removeUpdates(pi)
            pi.cancel()
        }
        Log.d(TAG, "Location updates stopped")
    }

    /**
     * Provides location updates status
     * @return
     */
    val isLocationUpdatesEnabled: Boolean
        get() {
            Log.d(TAG, "Is location updates enabled: " + (getLocationPendingIntent(false) != null))
            return getLocationPendingIntent(false) != null
        }

    // Returns last known location
    val lastKnownLocation: Location?
        get() {
            if (ActivityCompat.checkSelfPermission(
                    mContext,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    mContext,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return null
            }
            return mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        }

    // Crates PendingIntent for location updates
    private fun getLocationPendingIntent(shouldCreate: Boolean): PendingIntent? {
        val broadcast = Intent(ACTION_LOCATION_CHANGED)
        val flags = if (shouldCreate) 0 else PendingIntent.FLAG_NO_CREATE
        return PendingIntent.getBroadcast(mContext, 0, broadcast, flags)
    }

    companion object {
        private const val TAG = "mobilenetworkstracker"
        const val ACTION_LOCATION_CHANGED =
            "com.blogspot.droidcrib.mobilenetworkstracker.action.LOCATION_CHANGED"
    }
}

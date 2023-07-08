package com.shnaseri.strenghmap.presentation

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.loader.app.LoaderManager
import androidx.loader.content.Loader
import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.UiSettings
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.shnaseri.strenghmap.R
import com.shnaseri.strenghmap.controller.LocationReceiver
import com.shnaseri.strenghmap.controller.PositioningManager
import com.shnaseri.strenghmap.controller.TrackingManager
import com.shnaseri.strenghmap.db.AppDatabase
import com.shnaseri.strenghmap.loaders.PinpointLoader
import com.shnaseri.strenghmap.model.PinPoint
import com.shnaseri.strenghmap.telephony.CustomPhoneStateListener
import com.shnaseri.strenghmap.telephony.TelephonyInfo
import javax.inject.Inject

class TrackMapFragment @Inject constructor() :
    SupportMapFragment(),
    LoaderManager.LoaderCallbacks<List<*>?> {
    private var mGoogleMap: GoogleMap? = null
    private var mPrefs: SharedPreferences? = null
    private var mSelectedTrackId: Long = -1
    private var mLatSw = 0.0
    private var mLonSw = 0.0
    private var mLatNe = 0.0
    private var mLonNe = 0.0
    private var mCurrentZoom = 16.0f
    private var mTrackMapFragment: TrackMapFragment? = null

    @Inject
    lateinit var mTelephonyInfo: TelephonyInfo

    @Inject
    lateinit var mTrackingManager: TrackingManager

    @Inject
    lateinit var mPositioningManager: PositioningManager

    @Inject
    lateinit var mDatabaseManager: AppDatabase

    var mPinpointList: List<PinPoint>? = null
    private val mLocationReceiver: BroadcastReceiver =
        object : LocationReceiver(mTelephonyInfo, mTrackingManager, mDatabaseManager) {
            override fun onLocationReceived(context: Context?, loc: Location?, signalStrengths: Int) {
                super.onLocationReceived(context, loc, signalStrengths)
                if (!mPositioningManager.isLocationUpdatesEnabled) {
                    return
                }
                if (isVisible && mSelectedTrackId == -1L) {
                    if (loc != null) {
                        drawRealTimePoint(loc, signalStrengths)
                    }
                }
            }

            override fun onProviderEnabledChanged(enabled: Boolean) {
                super.onProviderEnabledChanged(enabled)
                val toastText: Int = if (enabled) R.string.gps_enabled else R.string.gps_disabled
                Toast.makeText(activity?.applicationContext, toastText, Toast.LENGTH_LONG)
                    .show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mTrackMapFragment = this
        mPrefs = activity?.getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val v: View = super.onCreateView(inflater, container, savedInstanceState)

        // save link to GoogleMap
         getMapAsync { p0 -> mGoogleMap = p0; }

        // show user location
        if (ActivityCompat.checkSelfPermission(
                requireActivity().applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION,
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireActivity().applicationContext,
                Manifest.permission.ACCESS_COARSE_LOCATION,
            ) != PackageManager.PERMISSION_GRANTED
        ) {
        }
        mGoogleMap?.isMyLocationEnabled = true

        // add zoom buttons on map
        val uiSettings: UiSettings? = mGoogleMap?.uiSettings
        uiSettings?.isZoomControlsEnabled = true
        return v
    }

    override fun onStart() {
        super.onStart()
        activity?.registerReceiver(
            mLocationReceiver,
            IntentFilter(PositioningManager.ACTION_LOCATION_CHANGED),
        )
        val lastKnownLocation: Location? = mPositioningManager.lastKnownLocation
        lastKnownLocation?.let { drawRealTimePoint(it, CustomPhoneStateListener.sSignalStrengths) }
    }

    override fun onResume() {
        super.onResume()
        mGoogleMap?.setOnCameraChangeListener {
            val bounds: LatLngBounds =
                mGoogleMap!!.projection.visibleRegion.latLngBounds
            mLatSw = bounds.southwest.latitude
            mLonSw = bounds.southwest.longitude
            mLatNe = bounds.northeast.latitude
            mLonNe = bounds.northeast.longitude
            mCurrentZoom = mGoogleMap!!.cameraPosition.zoom
        }
    }

    override fun onPause() {
        super.onPause()
        mSelectedTrackId = -1
    }

    override fun onStop() {
        super.onStop()
        activity?.unregisterReceiver(mLocationReceiver)
    }

    /**
     * Draws current location marker on map
     */
    private fun drawRealTimePoint(loc: Location, signalStrengths: Int) {
        val lat = loc.latitude
        val lon = loc.longitude

        // move camera to current location
        val latLng = LatLng(lat, lon)
        val cameraUpdate: CameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, mCurrentZoom)
        mGoogleMap?.moveCamera(cameraUpdate)

        // Draw point on map
        val circleOptions: CircleOptions = CircleOptions()
            .center(LatLng(lat, lon)).radius(15.0)
            .fillColor(pinPointColor(signalStrengths))
            .strokeWidth(0f)
        mGoogleMap?.addCircle(circleOptions)
    }

    /**
     * Draws markers for data queried in CursorLoader
     */
    private fun drawQueriedDataOnMap() {
        if (mSelectedTrackId != -1L) {
            mGoogleMap?.clear()
        }
        // positions bust
        for (i in mPinpointList!!.indices) {
            // Get data for marker
            val lat: Double = mPinpointList!![i].lat
            val lon: Double = mPinpointList!![i].longi
            val rxLevel = mPinpointList!![i].signalStrengths as Int

            // Draw signal level marker
            val circleOptions: CircleOptions = CircleOptions()
                .center(LatLng(lat, lon)).radius(15.0)
                .fillColor(pinPointColor(rxLevel))
                .strokeWidth(0f)
            mGoogleMap?.addCircle(circleOptions)
        }
    }

    /**
     * Shows selected track data on map
     */
    fun onTrackSelected(trackId: Long) {
        mSelectedTrackId = trackId
        if (trackId != -1L) {
            Log.d(TAG, "Selected track.ID $trackId")
            // get first point of track
            val pinPoint: PinPoint = mDatabaseManager.pinPointDao().findByKey("")
            val lat: Double = pinPoint.lat
            val lon: Double = pinPoint.longi
            Log.d(
                TAG,
                "FirstPinPointForTrack LAT  = $lat",
            )
            Log.d(
                TAG,
                "FirstPinPointForTrack LON  = $lon",
            )

            // move camera to first point
            val latLng = LatLng(lat, lon)
            val cameraUpdate: CameraUpdate =
                CameraUpdateFactory.newLatLngZoom(latLng, mCurrentZoom)
            mGoogleMap?.moveCamera(cameraUpdate)
        }
    }

    /**
     * Provides color value for signal level
     *
     * @param rxLevel
     * @return color resource id
     */
    private fun pinPointColor(rxLevel: Int): Int {
        var color = Color.WHITE
        if (rxLevel < -50 && rxLevel >= -70) {
            color = resources.getColor(R.color.legend_green)
        } // Green
        else if (rxLevel < -70 && rxLevel >= -80) {
            color = resources.getColor(R.color.legend_light_green)
        } // Light Green
        else if (rxLevel < -80 && rxLevel >= -90) {
            color = resources.getColor(R.color.legend_yellow)
        } // Yellow
        else if (rxLevel < -90 && rxLevel >= -100) {
            color = resources.getColor(R.color.legend_orange)
        } // Orange
        else if (rxLevel < -100 && rxLevel >= -115) {
            color = resources.getColor(R.color.legend_red)
        } // Red
        return color
    }

    override fun onCreateLoader(id: Int, args: Bundle?): PinpointLoader {
        return PinpointLoader(
            activity,
            mDatabaseManager,
            mSelectedTrackId,
            mLatSw,
            mLonSw,
            mLatNe,
            mLonNe,
        )
    }

    override fun onLoadFinished(loader: Loader<List<*>?>, data: List<*>?) {
        mPinpointList = data as List<PinPoint>?
        drawQueriedDataOnMap()
    }

    override fun onLoaderReset(loader: Loader<List<*>?>) {}

    companion object {
        private const val PREFS_FILE = "tracks"
        private const val PREF_CURRENT_TRACK_ID = "TrackingManager.currentTrackId"
        private const val LOAD_LOCATIONS = 0
        const val TAG = "mobilenetworkstracker"
    }
}

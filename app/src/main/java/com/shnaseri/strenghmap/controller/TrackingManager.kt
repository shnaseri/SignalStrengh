package com.shnaseri.strenghmap.controller

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.shnaseri.strenghmap.model.Track
import java.util.Date

class TrackingManager(mAppContext: Context) {
    private val mPrefs: SharedPreferences
    private var mCurrentTrackId: Long

    // constructor
    init {
        mPrefs = mAppContext.getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE)
        mCurrentTrackId = mPrefs.getLong(PREF_CURRENT_TRACK_ID, -1)
        Log.d(TAG, "Tracking Manager started")
    }

    /**
     * Starts network tracking
     */
    fun startTracking() {
        val track = Track()
        track.copy(startDate = Date())
        // Get Id
        mCurrentTrackId = 1
        // Save id in shared prefs
        mPrefs.edit().putLong(PREF_CURRENT_TRACK_ID, mCurrentTrackId).apply()
        mPrefs.edit().putBoolean(PREF_IS_TRACKING_ON, true).apply()
        val trackid = mPrefs.getLong(PREF_CURRENT_TRACK_ID, -1)
        val istracking = mPrefs.getBoolean(PREF_IS_TRACKING_ON, false)
        Log.d(
            TAG,
            "Tracking started, trackid = $trackid $istracking"
        )
    }

    /**
     * Stops network tracking
     */
    fun stopTracking() {
        mCurrentTrackId = -1
        mPrefs.edit().remove(PREF_CURRENT_TRACK_ID).apply()
        mPrefs.edit().remove(PREF_IS_TRACKING_ON).apply()
        val trackid = mPrefs.getLong(PREF_CURRENT_TRACK_ID, -1)
        val istracking = mPrefs.getBoolean(PREF_IS_TRACKING_ON, false)
        Log.d(
            TAG,
            "Tracking stopped trackid = $trackid $istracking"
        )
    }//        Log.d(TAG, "Is tracking enabled: " + (mPrefs.getBoolean(PREF_IS_TRACKING_ON, false)));

    /**
     * Provides network tracking status
     * @return
     */
    val isTrackingOn: Boolean
        get() =//        Log.d(TAG, "Is tracking enabled: " + (mPrefs.getBoolean(PREF_IS_TRACKING_ON, false)));
            mPrefs.getBoolean(PREF_IS_TRACKING_ON, false)

    companion object {
        private const val TAG = "mobilenetworkstracker"
        const val PREFS_FILE = "tracks"
        const val PREF_CURRENT_TRACK_ID = "TrackingManager.currentTrackId"
        private const val PREF_IS_TRACKING_ON = "TrackingManager.isTrackingOn"
    }
}

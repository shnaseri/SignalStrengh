package com.shnaseri.strenghmap.controller

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Parcelable
import android.os.PowerManager
import android.telephony.TelephonyManager
import android.util.Log
import androidx.core.content.ContextCompat.getSystemService
import com.shnaseri.strenghmap.MainActivity
import com.shnaseri.strenghmap.R
import com.shnaseri.strenghmap.db.AppDatabase
import com.shnaseri.strenghmap.model.Track
import com.shnaseri.strenghmap.telephony.TelephonyInfo
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class LocationReceiver(
    private var mTelephonyInfo: TelephonyInfo,
    private var mTrackingManager: TrackingManager,
    private var mDatabaseManager: AppDatabase,
) : BroadcastReceiver() {
    private var mSignalStrenghts = 0
    private var mLac: String? = null
    private var mCi: String? = null
    private var mTerminal: String? = null
    private var mLat = 0.0
    private var mLon = 0.0
    private var mEventTime: String? = null
    private var mOperatorName: String? = null
    private var mNotificationManager: NotificationManager? = null
    private var mSdf: SimpleDateFormat? = null
    private var mContext: Context? = null

    override fun onReceive(context: Context, intent: Intent) {
        mContext = context

        // Initialize notification manager
        mNotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val action = intent.action

        // Stop notifications by command
        if (action == ACTION_STOP_NOTIFICATION) {
            mNotificationManager!!.cancel(ID_NOTIFICATION_1)
            return
        } else if (action == ACTION_START_NOTIFICATION) {
            startNotification()
            return
        }

        // use PinPoint extra if available
        val loc = intent
            .getParcelableExtra<Parcelable>(LocationManager.KEY_LOCATION_CHANGED) as Location?
        if (loc != null) {
            // Get coordinates
            mLat = loc.latitude
            mLon = loc.longitude

//            Log.d(TAG, "Location received : " + mLat + " " + mLon);

            // Get event time
            mSdf = SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.US)
            mEventTime = mSdf!!.format(Date())

            // Get terminal info
            val manufacturer = Build.MANUFACTURER
            val model = Build.MODEL
            mTerminal = "$manufacturer;$model"

            // Enabling WakeLock mode
            val screenLock = (context.getSystemService(Context.POWER_SERVICE) as PowerManager)
                .newWakeLock(
                    PowerManager.SCREEN_DIM_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP,
                    "shnaseriApp:Power_service_location_strangh_map",
                )
            screenLock.acquire(10*60*1000L /*10 minutes*/)

            // Get telephony data
            mOperatorName = mTelephonyInfo.networkOperator
            mLac = mTelephonyInfo.lac
            mCi = mTelephonyInfo.ci
            val networkTypeForJSON: String = mTelephonyInfo.networkTypeForJSON
            mSignalStrenghts = mTelephonyInfo.signalStrengths

            // Release WakeLock mode
            screenLock.release()

            // Used for UI update
            onLocationReceived(context, loc, mSignalStrenghts)

            // Do if tracking enabled
            if (mTrackingManager.isTrackingOn) {
                val prefs = mContext!!.getSharedPreferences(
                    TrackingManager.PREFS_FILE,
                    Context.MODE_PRIVATE,
                )
                val trackId = prefs.getLong(TrackingManager.PREF_CURRENT_TRACK_ID, -1)
                Log.d(TAG, "Track ID: $trackId")
//                val track: Track = mDatabaseManager.queryTrack(trackId)
//                // Add PinPoint record to database
//                mDatabaseManager.insert(
//                    trackId, mSignalStrenghts, networkTypeForJSON,
//                    mLac, mCi, mTerminal, mLat, mLon, mOperatorName, track,
//                )
                startNotification()
            } else {
                Log.v(TAG, "PinPoint received with no tracking run; ignoring")
            }
            return
        }

        // Something other happened if we are here
        if (intent.hasExtra(LocationManager.KEY_PROVIDER_ENABLED)) {
            val enabled = intent
                .getBooleanExtra(LocationManager.KEY_PROVIDER_ENABLED, false)
            onProviderEnabledChanged(enabled)
        }
    }

    protected fun onLocationReceived(context: Context?, loc: Location?, signalStrengths: Int) {}
    protected fun onProviderEnabledChanged(enabled: Boolean) {
        Log.d(TAG, "Provider " + if (enabled) "enabled" else "disabled")
    }

    /**
     * Sends notification of current tracking status
     */
    private fun startNotification() {
        mSdf = SimpleDateFormat("dd.MM HH:mm:ss", Locale.US)
        mEventTime = mSdf!!.format(Date())
        val ticker = mContext!!.resources.getString(R.string.notif_ticker)
        val title = mContext!!.resources.getString(R.string.notif_title)
        val text = mContext!!.resources.getString(R.string.notif_text)
        val intent = Intent(mContext, MainActivity::class.java)
        val pi = PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        val builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(mContext, "1202")
        } else {
            Notification.Builder(mContext).setSound(null)
        }
        val notification: Notification = builder
            .setTicker(ticker)
            .setContentTitle(title)
            .setContentText("$text $mEventTime")
            .setContentIntent(pi)
            .setAutoCancel(false)
            .setOngoing(true)
            .build()
        mNotificationManager!!.notify(ID_NOTIFICATION_1, notification)
    }

    companion object {
        private const val TAG = "mobilenetworkstracker"
        const val ACTION_STOP_NOTIFICATION =
            "com.blogspot.droidcrib.mobilenetworkstracker.action.STOP_NOTIFICATION"
        const val ACTION_START_NOTIFICATION =
            "com.blogspot.droidcrib.mobilenetworkstracker.action.START_NOTIFICATION"
        private const val ID_NOTIFICATION_1 = 555
    }
}

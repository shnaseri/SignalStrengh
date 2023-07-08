package com.shnaseri.strenghmap.telephony

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import android.telephony.gsm.GsmCellLocation
import androidx.core.app.ActivityCompat
import com.shnaseri.strenghmap.R

class TelephonyInfo(
    private val mContext: Context,
    customPhoneStateListener: CustomPhoneStateListener?,
) {
    private var mTelephonyManager: TelephonyManager?
    private var mCustomPhoneStateListener: CustomPhoneStateListener?

    init {
        mCustomPhoneStateListener = customPhoneStateListener

        // Provide system service to telephony manager
        mTelephonyManager = mContext.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

        // Specify events to listen
        mTelephonyManager!!.listen(
            mCustomPhoneStateListener,
            PhoneStateListener.LISTEN_SIGNAL_STRENGTHS,
        )
    }

    /**
     * Provides network Operator name
     * @return
     */
    val networkOperator: String
        get() = try {
            mTelephonyManager!!.networkOperatorName
        } catch (e: NullPointerException) {
            mContext.resources.getString(R.string.unknown)
        }

    /**
     * Provides Location Area
     * @return
     */
    val lac: String
        get() {
            return try {
                if (ActivityCompat.checkSelfPermission(
                        mContext,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {

                }
                   val cellLocation =  mTelephonyManager!!.cellLocation as GsmCellLocation
                val lac = cellLocation.lac and 0xffff
                lac.toString()
            } catch (e: NullPointerException) {
                mContext.resources.getString(R.string.unknown)
            }
        }

    /**
     * Provides Cell Identity
     * @return
     */
    val ci: String
        get() {
            return try {
                 if (ActivityCompat.checkSelfPermission(
                        mContext,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                }
                val cellLocation = mTelephonyManager!!.cellLocation as GsmCellLocation
                val ci = cellLocation.cid and 0xffff
                ci.toString()
            } catch (e: NullPointerException) {
                mContext.resources.getString(R.string.unknown)
            }
        }

    /**
     * Provides mobile network type
     * @return
     */
    val networkType: String
        get() {
            return try {
                if (ActivityCompat.checkSelfPermission(
                        mContext,
                        Manifest.permission.READ_PHONE_STATE
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                }
                readNetworkType(mTelephonyManager!!.networkType)
            } catch (e: NullPointerException) {
                mContext.resources.getString(R.string.unknown)
            }
        }

    /**
     * Provides mobile network type to return in JSON
     * @return
     */
    val networkTypeForJSON: String
        get() {
            return try {
                if (ActivityCompat.checkSelfPermission(
                        mContext,
                        Manifest.permission.READ_PHONE_STATE
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                }
                readNetworkTypeForJSON(mTelephonyManager!!.networkType)
            } catch (e: Exception) {
                e.printStackTrace()
                mContext.resources.getString(R.string.unknown)
            }
        }

    /**
     * Provides network country information according to ISO
     * @return
     */
    val countryIso: String
        get() {
            return try {
                mTelephonyManager!!.networkCountryIso
            } catch (e: NullPointerException) {
                mContext.resources.getString(R.string.unknown)
            }
        }

    /** TODO: swap with interface
     * Provides network signal strengths in dBm
     * @return
     */
    val signalStrengths: Int
        get() = CustomPhoneStateListener.sSignalStrengths

    fun stopListener() {
        // Unregister listener PhoneStateListener
        mTelephonyManager!!.listen(mCustomPhoneStateListener, PhoneStateListener.LISTEN_NONE)
        mTelephonyManager = null
        mCustomPhoneStateListener = null
    }

    /**
     * Converts network type integer constant to text value
     * Used for UI update
     */
    private fun readNetworkType(networkType: Int): String {
        when (networkType) {
            TelephonyManager.NETWORK_TYPE_EDGE -> return "2G EDGE"
            TelephonyManager.NETWORK_TYPE_GPRS -> return "2G GPRS"
            TelephonyManager.NETWORK_TYPE_HSDPA -> return "3G HSDPA"
            TelephonyManager.NETWORK_TYPE_HSPA -> return "3G HSPA"
            TelephonyManager.NETWORK_TYPE_HSPAP -> return "3G HSPA+"
            TelephonyManager.NETWORK_TYPE_HSUPA -> return "3G HSUPA"
            TelephonyManager.NETWORK_TYPE_UMTS -> return "3G UMTS"
            TelephonyManager.NETWORK_TYPE_1xRTT -> return "1xRTT"
            TelephonyManager.NETWORK_TYPE_CDMA -> return "CDMA"
            TelephonyManager.NETWORK_TYPE_EHRPD -> return "eHRPD"
            TelephonyManager.NETWORK_TYPE_EVDO_0 -> return "EVDO rev. 0"
            TelephonyManager.NETWORK_TYPE_EVDO_A -> return "EVDO rev. A"
            TelephonyManager.NETWORK_TYPE_EVDO_B -> return "EVDO rev. B"
            TelephonyManager.NETWORK_TYPE_IDEN -> return "iDen"
            TelephonyManager.NETWORK_TYPE_LTE -> return "LTE"
            TelephonyManager.NETWORK_TYPE_UNKNOWN -> return "Unknown"
        }
        throw RuntimeException("New type of network")
    }

    /**
     * Converts network type integer constant to text value
     * Used in JSON
     */
    private fun readNetworkTypeForJSON(networkType: Int): String {
        when (networkType) {
            TelephonyManager.NETWORK_TYPE_EDGE -> return "GSM"
            TelephonyManager.NETWORK_TYPE_GPRS -> return "GSM"
            TelephonyManager.NETWORK_TYPE_HSDPA -> return "UMTS"
            TelephonyManager.NETWORK_TYPE_HSPA -> return "UMTS"
            TelephonyManager.NETWORK_TYPE_HSPAP -> return "UMTS"
            TelephonyManager.NETWORK_TYPE_HSUPA -> return "UMTS"
            TelephonyManager.NETWORK_TYPE_UMTS -> return "UMTS"
            TelephonyManager.NETWORK_TYPE_1xRTT -> return "1xRTT"
            TelephonyManager.NETWORK_TYPE_CDMA -> return "CDMA"
            TelephonyManager.NETWORK_TYPE_EHRPD -> return "eHRPD"
            TelephonyManager.NETWORK_TYPE_EVDO_0 -> return "EVDO rev. 0"
            TelephonyManager.NETWORK_TYPE_EVDO_A -> return "EVDO rev. A"
            TelephonyManager.NETWORK_TYPE_EVDO_B -> return "EVDO rev. B"
            TelephonyManager.NETWORK_TYPE_IDEN -> return "iDen"
            TelephonyManager.NETWORK_TYPE_LTE -> return "LTE"
            TelephonyManager.NETWORK_TYPE_UNKNOWN -> return "Unknown"
        }
        throw RuntimeException("New type of network")
    }

    companion object {
        private const val TAG = "mobilenetworkstracker"
    }
}

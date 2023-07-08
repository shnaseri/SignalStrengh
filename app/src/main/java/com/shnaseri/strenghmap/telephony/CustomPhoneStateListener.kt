package com.shnaseri.strenghmap.telephony

import android.telephony.PhoneStateListener
import android.telephony.SignalStrength

class CustomPhoneStateListener : PhoneStateListener() {
    private var mListenerInterface: PhoneStateListenerInterface? = null
    fun setInterface(listenerInterface: PhoneStateListenerInterface?) {
        mListenerInterface = listenerInterface
    }

    override fun onSignalStrengthsChanged(signalStrengths: SignalStrength) {
        super.onSignalStrengthsChanged(signalStrengths)
        // Get Signal Strength, dBm
        var rssi = 0
        if (signalStrengths.gsmSignalStrength != 99) {
            rssi = signalStrengths.gsmSignalStrength * 2 - 113
            sSignalStrengths = rssi
        } else {
            rssi = signalStrengths.gsmSignalStrength
            sSignalStrengths = rssi
        }
        if (mListenerInterface != null) {
            mListenerInterface!!.signalStrengthsChanged(rssi)
        }
    }

    companion object {
        private const val TAG = "mobilenetworkstracker"
        var sSignalStrengths = 99
    }
}

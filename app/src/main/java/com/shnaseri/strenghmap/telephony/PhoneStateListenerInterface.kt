package com.shnaseri.strenghmap.telephony

interface PhoneStateListenerInterface {
    fun signalStrengthsChanged(signalStrengths: Int)
}
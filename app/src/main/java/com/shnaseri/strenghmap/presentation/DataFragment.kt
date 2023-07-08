package com.shnaseri.strenghmap.presentation

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TableLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.shnaseri.strenghmap.R
import com.shnaseri.strenghmap.controller.TrackingManager
import com.shnaseri.strenghmap.databinding.ActivityMainBinding
import com.shnaseri.strenghmap.db.AppDatabase
import com.shnaseri.strenghmap.model.PinPoint
import com.shnaseri.strenghmap.telephony.CustomPhoneStateListener
import com.shnaseri.strenghmap.telephony.PhoneStateListenerInterface
import com.shnaseri.strenghmap.telephony.TelephonyInfo
import java.util.Locale
import javax.inject.Inject

class DataFragment constructor(
    var mTrackingManager: TrackingManager,
    var mTelephonyInfo: TelephonyInfo,
    var mCustomPhoneStateListener: CustomPhoneStateListener,
    var mDatabaseManager: AppDatabase
) : Fragment(), PhoneStateListenerInterface {
    private var mSignalStrenghtsTextView: TextView? = null
    private var mNetworkTypeTextView: TextView? = null
    private var mOperatorTextView: TextView? = null
    private var mLacTextView: TextView? = null
    private var mCiTextView: TextView? = null
    private var mCountryTextView: TextView? = null
    private var mJsonPostUrl: EditText? = null
    var mTableLayout: TableLayout? = null
    private var mButtonJsonUpload: Button? = null
    private var mButtonStopService: Button? = null
    private var queryAll: Button? = null
    private lateinit var binding: ActivityMainBinding
    private var mTrackId: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true

        // Check Track identifier and get series object
        val args: Bundle? = arguments
        if (args != null) {
            val trackId = args.getLong(ARG_TRACK_ID, -1)
            if (trackId != -1L) {
                mTrackId = trackId
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val v: View = inflater.inflate(R.layout.fragment_data, container, false)
        mSignalStrenghtsTextView = v.findViewById<View>(R.id.tv_level) as TextView
        mNetworkTypeTextView = v.findViewById<View>(R.id.tv_type) as TextView
        mOperatorTextView = v.findViewById<View>(R.id.tv_operator) as TextView
        mLacTextView = v.findViewById<View>(R.id.tv_lac) as TextView
        mCiTextView = v.findViewById<View>(R.id.tv_ci) as TextView
        mCountryTextView = v.findViewById<View>(R.id.tv_country) as TextView
        mJsonPostUrl = v.findViewById<View>(R.id.json_post_url) as EditText
        mTableLayout = v.findViewById<View>(R.id.table_indicator) as TableLayout
        queryAll = v.findViewById<View>(R.id.query_all) as Button
        mButtonJsonUpload = v.findViewById<View>(R.id.json_upload) as Button
        mButtonStopService = v.findViewById<View>(R.id.stop_service) as Button
        return v
    }

    override fun onResume() {
        super.onResume()

        // set Interface for CustomPhoneStateListener
        mCustomPhoneStateListener.setInterface(this@DataFragment)
        updateUI()

        queryAll!!.setOnClickListener {
            val pinPoints: List<PinPoint> =
                mDatabaseManager.pinPointDao().getAll()
            Log.d(
                TAG,
                "Table PINPOINT size: " + pinPoints.size,
            )
            for (i in pinPoints.indices) {
                Log.d(
                    TAG,
                    (
                        "Table content: " +
                            pinPoints[i].zeroId
                        ) + " rssi= " + pinPoints[i].signalStrengths + " networkType= " + pinPoints[i].networkType + " lac= " + pinPoints[i].lac + " ci= " + pinPoints[i].ci + " terminal= " + pinPoints[i].terminal + " lat= " + pinPoints[i].lat + " lon=" + pinPoints[i].longi + " time= " + pinPoints[i].eventTime + " operator= " + pinPoints[i].operator + " country= " + pinPoints[i].country + " upload= " + pinPoints[i].upload + " track = " + pinPoints[i].track + " track ID = " + pinPoints[i].trackId,
                )
            }
        }
    }

    /**
     * Updates UI with actual data
     */
    fun updateUI() {
        val operator: String = mTelephonyInfo.networkOperator
        val netType: String = mTelephonyInfo.networkType
        val sigStr: Int = mTelephonyInfo.signalStrengths
        val lac: String = mTelephonyInfo.lac
        val ci: String = mTelephonyInfo.ci
        val country: String = mTelephonyInfo.countryIso.uppercase(Locale.getDefault())
        mOperatorTextView!!.text = operator
        mNetworkTypeTextView!!.text = netType
        mSignalStrenghtsTextView!!.text = signalLevelExplain(sigStr) + " (" + sigStr + ")"
        mLacTextView!!.text = lac
        mCiTextView!!.text = ci
        mCountryTextView!!.text = country

        // Signal level indicator is represented by TableLayout with 64 rows
        val indicatorLevel: Int = mTelephonyInfo.signalStrengths * -1 - 51
        for (i in 0 until mTableLayout!!.childCount) {
            if (i <= indicatorLevel) {
                mTableLayout!!.getChildAt(i)
                    .setBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.color_blue_gray_100,
                        ),
                    )
                if (i in 49..63) {
                    mTableLayout!!.getChildAt(i)
                        .setBackgroundColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.legend_red_idle,
                            ),
                        )
                } else if (i in 39..48) {
                    mTableLayout!!.getChildAt(i)
                        .setBackgroundColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.legend_orange_idle,
                            ),
                        )
                } else if (i in 29..38) {
                    mTableLayout!!.getChildAt(i)
                        .setBackgroundColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.legend_yellow_idle,
                            ),
                        )
                } else if (i in 19..28) {
                    mTableLayout!!.getChildAt(i)
                        .setBackgroundColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.legend_light_green_idle,
                            ),
                        )
                } else if (i <= 18) {
                    mTableLayout!!.getChildAt(i)
                        .setBackgroundColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.legend_green_idle,
                            ),
                        )
                }
            } else {
                if (i in 49..63) {
                    mTableLayout!!.getChildAt(i)
                        .setBackgroundColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.legend_red,
                            ),
                        )
                } else if (i in 39..48) {
                    mTableLayout!!.getChildAt(i)
                        .setBackgroundColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.legend_orange,
                            ),
                        )
                } else if (i in 29..38) {
                    mTableLayout!!.getChildAt(i)
                        .setBackgroundColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.legend_yellow,
                            ),
                        )
                } else if (i in 19..28) {
                    mTableLayout!!.getChildAt(i)
                        .setBackgroundColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.legend_light_green,
                            ),
                        )
                } else if (i <= 18) {
                    mTableLayout!!.getChildAt(i)
                        .setBackgroundColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.legend_green,
                            ),
                        )
                }
            }
        }
    }

    /**
     * Provides human readable explanation for signal level value
     * @param rxLevel input signal level
     * @return signal level value meaning
     */
    private fun signalLevelExplain(rxLevel: Int): String {
        var description = ""
        if (rxLevel < -50 && rxLevel >= -70) {
            description = resources.getString(R.string.excellent)
        } else if (rxLevel < -70 && rxLevel >= -80) {
            description = resources.getString(R.string.good)
        } else if (rxLevel < -80 && rxLevel >= -90) {
            description = resources.getString(R.string.sufficient)
        } else if (rxLevel < -90 && rxLevel >= -100) {
            description = resources.getString(R.string.poor)
        } else if (rxLevel < -100 && rxLevel >= -115) {
            description = resources.getString(R.string.bad)
        } else if (rxLevel == 99) {
            description = resources.getString(R.string.no_signal)
        }
        return description
    }

    override fun signalStrengthsChanged(signalStrengths: Int) {
        // Update UI on Signal Strengths changes
        if (this.isVisible) {
            updateUI()
        }
    }

    companion object {
        const val TAG = "mobilenetworkstracker"
        private const val ARG_TRACK_ID = "TRACK_ID"
    }
}
